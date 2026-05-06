package com.data_management;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads output files produced by HealthDataSimulator's FileOutputStrategy.
 */
public class FileDataReader implements DataReader {
    private final Path directory;

    public FileDataReader(Path directory) {
        this.directory = directory;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        if (dataStorage == null) {
            throw new IllegalArgumentException("dataStorage must not be null");
        }
        if (!Files.isDirectory(directory)) {
            throw new IOException("Data directory does not exist: " + directory);
        }

        File folder = directory.toFile();
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                readFile(file.toPath(), dataStorage);
            }
        }
    }

    private void readFile(Path file, DataStorage dataStorage) throws IOException {
        List<String> lines = Files.readAllLines(file);

        for (String line : lines) {
            if (line.startsWith("Patient ID: ")) {
                parseLine(line, dataStorage);
            }
        }
    }

    private void parseLine(String line, DataStorage dataStorage) {
        String[] parts = line.split(", ");

        if (parts.length != 4) {
            return;
        }

        if (!parts[0].startsWith("Patient ID: ")) {
            return;
        }
        if (!parts[1].startsWith("Timestamp: ")) {
            return;
        }
        if (!parts[2].startsWith("Label: ")) {
            return;
        }
        if (!parts[3].startsWith("Data: ")) {
            return;
        }

        try {
            String patientText = parts[0].substring("Patient ID: ".length()).trim();
            String timestampText = parts[1].substring("Timestamp: ".length()).trim();
            String label = parts[2].substring("Label: ".length()).trim();
            String rawData = parts[3].substring("Data: ".length()).trim();

            int patientId = Integer.parseInt(patientText);
            long timestamp = Long.parseLong(timestampText);
            double value;

            if (label.equalsIgnoreCase("Alert") && rawData.equalsIgnoreCase("triggered")) {
                value = 1.0;
            } else if (label.equalsIgnoreCase("Alert") && rawData.equalsIgnoreCase("resolved")) {
                value = 0.0;
            } else {
                rawData = rawData.replace("%", "");
                value = Double.parseDouble(rawData);
            }

            dataStorage.addPatientData(patientId, value, label, timestamp);
        } catch (NumberFormatException e) {
            // Skip lines that do not contain valid numbers.
        }
    }
}
