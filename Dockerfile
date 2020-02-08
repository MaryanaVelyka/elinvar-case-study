FROM java:8-jdk-alpine

COPY src /src

RUN javac src/main/java/com/mvelyka/elinvar/LogAnalyzer.java
ENTRYPOINT ["java", "-classpath", "src/main/java", "com.mvelyka.elinvar.LogAnalyzer"]
