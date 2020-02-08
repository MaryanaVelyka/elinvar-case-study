# Elinvar Case Study
The repository contains the script written in Java to parse the log file and extract following information:
1. Name of service 
2. Amount of request to service 
3. Max time of request execution

## Prerequisites
Please verify that you have `java`, `docker` and `maven` installed.

## Log Analyzer

### Running
There are 3 options to run Log Analyzer

#### Image from dockerhub
Image is pushed to the dockerhub.
 
Run `docker run -v /tmp/:/tmp/ mvelyka/elinvar-case-study:1.0-SNAPSHOT /tmp/log.file`.
 
Where `/tmp/log.file` is file located on your local filesystem.

#### Building own image
To build you own image run either:
1. `docker build -t elinvar-case-study .` from the root directory
2. `mwn clean install` from the root directory
 
To run build image run `docker run -v /tmp/:/tmp/ elinvar-case-study:1.0-SNAPSHOT /tmp/log.file` .
 
Where `/tmp/log.file` is file located on your local filesystem.

#### Running via command line
1. `cd ../src/main/java/com/mvelyka/elinvar`
2. `javac LogAnalyzer.java`
3. `cd ../src/main/java`
4. `java com.mvelyka.elinvar.LogAnalyzer path/to/the/file/log.file`

