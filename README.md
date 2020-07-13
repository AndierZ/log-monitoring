# Objective
Application for log monitoring and alert generation.

# Requirements
- Expect large log files
- Evaluate and generate alerts over fixed time intervals
- Evaluate and generate alerts based on a rolling window
- Alert logic will be modular and easily modified/added
- Alert details will be configurable
- Maintainability and scalability
- Unit test coverage
- Simple packaging and distribution

# Design considerations
- Log file is processed by a separate thread in a separate process, so entire process is asynchronous.
- Communicate over the network so we can easily scale up by having multiple producers (log file parsing) and consumers (generating alerts)
- Support long running producers if so desired
- Support the parsing of different log files into different messages
- Support the addition of new alerts for different messages
- Have alerts and parameters configurable
- Be as memory efficient as possible, for example, by avoiding creating new objects for each log message

# Code structure and explanation
Project is largely comprised oftwo parts
- LogReaderApp (producer): Gets all log files and creates MessageDistributionThread for each file 
  - MessageDistributionThread: Parses the file and passes each line to LogEntrySender
    - LogEntrySender: Converts line to binary message and sends to the output stream
- ConsoleAlertingApp (consumer): Awaits incoming connection and creates MessageProcessingThread for each new connection
  - MessageProcessingThread: Parses binary message into readable objects and pass on to MessageHandler
    - MessageHandler: Route each message through the list of <? extends StatsMonitor>
      - <? extends StatsMonitor>: Processes each log message and computes statistics

# Maintainability and Scalability
- Adding new alerts
  - New alerting logic can be added by sub-classing one of the super classes
    - RollingStatsMonitor: For generating alerts over a rolling window
    - KeyedRollingStatsMonitor: For generating alerts based on keys specified by the user, over a rolling window
    - RankedFixedStatsMonitor: For genearting alerts with ranked values over fixed windows
- Adding new messages
  - New message can be created by following how the LogEntry message is supported. Framework already supports it.
  - If the number of different messages becomes big, we could consider using code generation for generating message codec.
- Scale horizontally
  - Different producers for different log files
  - Different producers for the same file but different fields
    - For example, route all (timestamp, remoteuser) to one consumer, route all (timestamp, status) to a different consumer.

# Improvements
- Add more test cases
- Introduce heartbeats: Currently the "clock" on the consumer is essentially driven by the log messages themselves, and each alert has a smallest "step size". For example fixed alerts will only fire every 10 seconds; even rolling alerts has a smallest unit by which it aggregates messages, which by default is 1 second. As a result, the last few log messages will always be left in the consumer's "cache" because the clock couldn't tick to the next step. One way to fix this is to have the producer send regular heartbeat messages with its timestamp.
- Think about dealing with log lines with timestamps out of order: If the log file is written by multiple threads the timestamp might be out of order. The intuitive way is to add logic on the log generation side to ensure every log line is written sequentially.
- Use codegen for message codec: Ideally we could specify the message structure in config file and have code generation for the actual building and parsing logic, e.g. flat buffer.
- Introduce redundancy for the consumer: When a consumer goes down, producer should retry with a list of backup consumers.
- Make backup consumers always in sync with the primary consumer: So the underlying states for the alerts can be preserved. One option could have all the backup instances listen to the same messages from the producer, and only have the primary instance generate alert. When it goes down, implement leader election logic to promote one backup instance as the leader.
- Manage config files separately: Config files are specified when launching the jar, so we can manage them separately. Employ proper version control, build pipe lines for updating and distributing config changes to all machines.

# How to run
- Run in dev environment
  - Open Intellij and choose ```open```
  - Select the top level "log-monitoring" directory
  - Click on ```Build``` -> ```Build Project```
  - Run demo.ConsoleAlertingAppLauncher
  - Run demo.LogReaderAppLauncher
- Run with the jar
  - Open terminal and go to the project directory
  - ``` ./gradlew shadowJar```
  - Modify the ```log_reader_app.json``` file in the resource folder. Remove ```"log_files_in_resource": true```, and update ```log_files``` to point to the absolute path of the log file to be parsed
  - ```java -jar build/libs/shadow-jar-1.0-SNAPSHOT.jar apps.consumer.ConsoleAlertingApp ./src/main/resources/config/console_alerting_app.json```
  - ```java -jar build/libs/shadow-jar-1.0-SNAPSHOT.jar apps.consumer.LogReaderApp ./src/main/resources/config/log_reader_app.json```
