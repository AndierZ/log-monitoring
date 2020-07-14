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
- Configure and instantiate alerting modules using config
- Be as memory efficient as possible, for example, by avoiding creating new objects for each log message

# Code structure
Project is largely comprised of two parts
- LogReaderApp (producer): Gets all log files and creates MessageDistributionThread for each file 
  - MessageDistributionThread: Parses the file and passes each line to LogEntrySender
    - LogEntrySender: Converts line to binary message and sends to the output stream
- ConsoleAlertingApp (consumer): Awaits incoming connection and creates MessageProcessingThread for each new connection
  - MessageProcessingThread: Parses binary message into readable objects and passes on to MessageHandler
    - MessageHandler: Routes each message through a list of <? extends StatsMonitor>
      - <? extends StatsMonitor>: Modules with business logic for generating alerts
- Implemented alerts:
  - AverageHitsMonitor: Keeps track of the average hits per second over a 120s rolling window. Alerts if exceeds 10 hits per second.
  - AverageHitsBySectionMonitor: Keeps track of the average hits per second by section over a 10s rolling window. Alerts if exceeds 10 hits per second.
  - AverageHitsByRemoteHostMonitor: Keeps track of the average hits per second by remote host over a 10s rolling window. Alerts if exceeds 10 hits per second.
  - TotalBytesMonitor: Keeps track of the total bytes over a 120s rolling window. Alerts if it exceeds 1Mb
  - RankedSectionHitsMonitor: Checks every 60s and prints the top 3 sections with most hits for the preceding 60s interval.
# Maintainability and Scalability
- Adding new alerts
  - New module can be added by sub-classing one of the super classes
    - RollingStatsMonitor: For generating alerts over a rolling window
    - KeyedRollingStatsMonitor: For generating keyed alerts over a rolling window
    - RankedFixedStatsMonitor: For genearting alerts with ranked values over fixed windows
  - Add the new module in the ```monitor_list``` property
  - Code can be made more generic to eliminate the need to write subclasses altogether; But that'll make unit testing a bit harder and the whole structure more obscure
- Adding new messages
  - If we need to process different log files with completely different content
  - New message can be created by following how the LogEntry message is supported
  - If the number of different messages becomes big, we could consider using code generation for generating message codec
- Scale horizontally
  - Across different log files
    - For example, we could setup 10 producers for 10 log files and 4 consumers
  - Across different fields in the same log file
    - Not fully supported, but the framework allows for the routing of different fields to different consumers. For example, we might want to send to one consumer (timestamp, remotehost) if remotehost is in a certain list, and send to another consumer (timestamp, status) when status is bad, and all (timestamp, section) to a 3rd consumer

# Improvements
- Add more unit tests
- Better deal with timestamps that are out of order. From the sample data it looks like each remote host was logged separately and as a result between different remote hosts the timestamps are slightly out of order. In the current logic if it receives a timestamp older than the "current" timestamp, it'll use the current timestamp. A potentially better way to handle it is to partition the input data by remote host, so within each partition timestamps are consistent. But then different partitions could be running at different "clocks" and be out of sync, so merge their stats might not be trivial.
- Introduce heartbeats: Currently the "clock" on the consumer side is essentially driven by the log messages themselves, and each alert has a smallest "step size". For example fixed alerts will only be evaluated every 10 seconds; even rolling alerts has a smallest unit by which it aggregates messages, which by default is 1 second. As a result, the last few log messages will always be left in the consumer's "cache" because the clock couldn't tick to the next step. One way to fix this is to have the producer send regular heartbeats with its timestamp.
- Output rolling averages as streams and then aggregate. Instead of calculating the rolling averages internally and generating alerts directly from that, outputs the rolling average as streams. And from there we can have another component generate alerts, rank or do arithmics on them.
- Add regression tests where we instantiate both the producer and consumer and simulate the entire process. A concept of ```Context``` is already in place which is supposed to serve as an interface that catches all inbound/outbound traffic. So we could supply the regression tests with dummy loopback sockets via the ```Context``` and have the two applicaitons communicate directly without having to establish an actual network connection.
- Use codegen for message codec: Ideally we could specify the message schema in config file and have code generation to produce the message building and parsing logic, e.g. flat buffer.
- Introduce redundancy for the consumer: When a consumer goes down, producer should retry with a list of backup consumers.
- Make backup consumers always in sync with the primary consumer so the underlying states for the alerts can be preserved. One option would be to have all the backup instances listen to the same messages from the producer, and only have the primary instance generate alerts. When primary goes down, implement leader election logic to promote one backup instance as the leader.
- Manage config files separately: Config files are specified when launching the jar, so we can manage them separately. Employ proper version control and deploy pipe lines for updating and distributing config changes across machines.

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
  - ```java -jar build/libs/shadow-jar-1.0-SNAPSHOT.jar apps.producer.LogReaderApp ./src/main/resources/config/log_reader_app.json```
