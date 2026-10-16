package com.medikiosk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class MediKioskApplication {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(MediKioskApplication.class, args);
    }

    /**
     * Loads key-value pairs from .env file into Java System Properties
     * so Spring Boot can resolve them via ${PROPERTY_NAME} syntax.
     */
    private static void loadDotEnv() {
        File[] candidateLocations = new File[] {
            new File(".env"),
            new File("../.env"),
            new File("backend/.env")
        };

        for (File file : candidateLocations) {
            if (file.exists() && file.isFile()) {
                System.out.println("[INFO] Loading environment configuration from " + file.getAbsolutePath());
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String value = line.substring(eqIdx + 1).trim();
                            // Remove optional enclosing quotes
                            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                (value.startsWith("'") && value.endsWith("'"))) {
                                if (value.length() >= 2) {
                                    value = value.substring(1, value.length() - 1);
                                }
                            }
                            // Do not override existing system properties or OS environment variables
                            if (System.getProperty(key) == null && System.getenv(key) == null) {
                                System.setProperty(key, value);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("[WARN] Failed to load .env from " + file.getAbsolutePath() + ": " + e.getMessage());
                }
                break;
            }
        }
    }
}

