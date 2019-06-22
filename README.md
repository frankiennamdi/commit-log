# Description

This project simulates read and write log servers. The write server produces data
and the read server consumes it.

# Technologies
1. gradle (included)
2. Java 8.x - chosen for some of its appealing functionality like streams, functions and method references.
3. Developed with IDEA Intellij - chosen as my IDE of choice.
4. Spring Boot

# Tests

Test are included and test the reading and writing of log information

# Build

```
./gradlew clean build"
```

# Configuration

The configuration property file is located in **config** directory. If the path of the log
location is not absolute the log is written in the present working directory.

# Execution
Below are some examples of how to execute the application. The log file is always
recreated, in the future we can configure it to append and roll over as improvements.

## In Terminal 1:

Start the write server

```
./log-server -mode WRITER
```

**Output:**
```
[WRITER-1] - A: 234: TtMsRrZfFVSf
[WRITER-2] - A: 233: ZpULlFFYGYQnVjyrAVPzhhzSeJ
[WRITER-5] - B: 351: YsWdapXEaBTxpkSk
[WRITER-3] - B: 352: IzUsvuumNguNxTgoKZ
[WRITER-2] - A: 236: ffhbujybwFnJZ
[WRITER-1] - A: 235: HORpfIPEDfX

```

## In Terminal 2:

Start the write server

```
./log-server -mode READER
```

**Output**
```
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