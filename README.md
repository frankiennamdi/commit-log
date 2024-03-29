# Description

This project simulates read and write log services. The write service produces data
and the read service consumes it.

# Technologies
1. gradle (included)
2. Java 8.x - chosen for some of its appealing functionality like streams, functions and method references.
3. Developed with IDEA Intellij - chosen as my IDE of choice.
4. Spring Boot

# Tests

Test are included and test the reading and writing of log information.

# Build

```
./gradlew clean build"
```

executable is placed here **build/libs/commit-log-1.0-SNAPSHOT.jar**

# Configuration

The configuration property file is located in **config/application.properties** directory.
If the path of the log location is not absolute the log is written in the present working directory.
You can change the content of this file and restart the service.

The current content:

```
app.log-service.writers.A=2
app.log-service.writers.B=3
app.log-service.readers.A=2
app.log-service.readers.B=3
app.log-service.logPath=commit.log
```


# Constraints

The writer must be started before the reader. If this order is not followed the reader shutdowns
after a failed check.

# ./log-service

Is a wrapper script around commit uber jar for convenience. If you do not have
a linux terminal with exec command, you can execute the code directly through
the following command:

```
java -jar build/libs/commit-log-1.0-SNAPSHOT.jar -mode [WRITER|READER]
```

# Execution
Here are instructions on how to execute the application. The log file is always
recreated, in the future we can configure it to append and roll over as improvements.

## In Terminal 1:

Start the write service

```
./log-service -mode WRITER
```

**Output:**
```
[main] - Starting Log Service in WRITER mode
[main] - Log Location /Users/[WORKING DIR]/commit-log/commit.log
[main] - Writers [A=2, B=3]
[Thread-3] - Running in WRITER mode
[WRITER-1] - A: 234: TtMsRrZfFVSf
[WRITER-2] - A: 233: ZpULlFFYGYQnVjyrAVPzhhzSeJ
[WRITER-5] - B: 351: YsWdapXEaBTxpkSk
[WRITER-3] - B: 352: IzUsvuumNguNxTgoKZ
[WRITER-2] - A: 236: ffhbujybwFnJZ
[WRITER-1] - A: 235: HORpfIPEDfX

```

## In Terminal 2:

Start the write service

```
./log-service -mode READER
```

**Output**
```
[main] - Starting Log Service in READER mode
[main] - Log Location /Users/[WORKING DIR]/commit-log/commit.log
[main] - Readers [A=2, B=3]
[Thread-3] - Running in READER mode
[READER-2] - A: 109: TSpWtrjouG
[READER-2] - A: 110: yNnGsuZorhOOCjdWSLvbCBX
[READER-2] - A: 111: VhNJqXpcJRCaIRWupKoTeWp
[READER-1] - A: 112: ExXzIAsZAWH
[READER-1] - A: 113: NVqvyKyiddJDKFy
[READER-1] - A: 114: xizOIzAWdyMJFUwIfBvDDqPg
[READER-1] - A: 115: dYwuLiMsBhsU
[READER-2] - A: 116: gTEMYnQDVRJGbfEkwFfIQKOFHouoRP
```

# Shutdown

```
Ctrl+c
```

# Future Improvements

1. More testing.
2. Rolling file to backup based on date or size or start time.
