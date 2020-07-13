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
- Each log file should be processed by a separate thread in a separate process, decoupled from the alerting logic
- Communicate over the network so we can easily scale up by having multiple producers (log file parsing) and consumers (generating alerts)
- Support long running producers if so desired
- Support the parsing of different log files into different messages
- Support the addition of new alerts for different messages
- Have alerts and parameters configurable
- Be as memory efficient as possible, for example, by avoiding creating new objects for each log message

# Code structure and explanation

# Maintainability and Scalability
- New messages
- Sharding: by file, by fields in the same file

# Improvements
- Add more test cases
- Introduce heartbeats: Currently the "clock" on the consumer is essentially driven by the log messages themselves, and each alert has a smallest "step size". For example fixed alerts will only fire every 10 seconds; even rolling alerts has a smallest unit by which it aggregates messages, which by default is 1 second. As a result, the last few log messages will always be left in the consumer's "cache" because the clock couldn't tick to the next step. One way to fix this is to have the producer send heartbeat messages with its timestamp.
- Think about dealing with log lines with timestamps out of order: If the log file is written by multiple threads the timestamp might be out of order. The intuitive way is to add logic on the log generation side to ensure every log line is written sequentially.
- Use codegen for message codec: Ideally we could specify the message structure in config file and have code generation for the actual building and parsing logic, e.g. flat buffer.
- Introduce redundancy for the consumer: When a consumer goes down, producer should retry with a list of backup consumers.
- Make backup consumers always in sync with the primary consumer: So the underlying states for the alerts can be preserved. One option could have all the backup instances listen to the same messages from the producer, and only have the primary instance generate alert. When it goes down, implement leader election logic to promote one backup instance as the leader.
- Manage config files separately: Config files are specified when launching the jar, so we can manage them separate from the jar. Employ proper version control for the configs, build pipe line for updating and distributing config changes to all machines.
Manage the apps using kubernetes

# How to run
