package com.mvelyka.elinvar;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalyzer {
    public static void main(String[] args) {

        if (args == null || args.length != 1) {
            System.out.println("Wrong number of parameters. Please provide exactly one file to parse");
            System.exit(0);
        }

        Path file = Paths.get(args[0]);
        Map<String, LocalDateTime> requestsEntryTime = new HashMap<>();
        Map<String, ServiceStats> stats = new HashMap<>();

        // regexr.com/4tset
        Pattern p = Pattern.compile("^(\\d{4}-\\d{2}\\-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\,\\d{3}).+\\[.+\\] (exit|entry).+\\((\\w+):(\\w+)\\)$");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss,SSS");

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    String datetime = m.group(1);
                    String action = m.group(2);
                    String serviceName = m.group(3);
                    String requestId = m.group(4);

                    String key = serviceName + requestId;
                    LocalDateTime actionTime = LocalDateTime.parse(datetime, formatter);

                    if ("entry".equals(action)) {
                        requestsEntryTime.put(key, actionTime);
                    } else if("exit".equals(action)) {
                        LocalDateTime entryTime = requestsEntryTime.get(key);

                        // It's possible to have partial log file with exit lines without entry lines
                        if (entryTime != null) {
                            long durationInMillis = ChronoUnit.MILLIS.between(entryTime, actionTime);

                            ServiceStats serviceStats = stats.getOrDefault(serviceName, new ServiceStats());
                            serviceStats.requestCount++;
                            serviceStats.maxRequestDuration = Math.max(serviceStats.maxRequestDuration, durationInMillis);
                            stats.put(serviceName, serviceStats);
                        }
                    }

                }
            }
        } catch (IOException x) {
            System.err.format("Could not read file: %s", file.toString());
        }

        for (Map.Entry<String, ServiceStats> entry : stats.entrySet()) {
            System.out.printf("Service: %s, requests count: %s, max request duration ms: %s\n",
                    entry.getKey(), entry.getValue().requestCount, entry.getValue().maxRequestDuration);
        }

    }

    private static class ServiceStats {
        int requestCount;
        long maxRequestDuration;
    }
}
