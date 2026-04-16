package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Writes generated health measurements to files.
 * Each measurement type is written to a separate text file in the specified
 * directory.
 */

public class FileOutputStrategy implements OutputStrategy {
    // changed field name to camelCase
    private final String baseDirectory;
    // changed field name to camelCase and made it private
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Creates a file output strategy.
     * 
     * @param baseDirectory the directory where output files will be stored
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes one generated measurement to a file.
     * The file is selected based on the measurement label.If an error occurs
     * while creating the directory or writing to the file, it is logged and the
     * method returns without throwing the exception further.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time when the measurement was generated, in milliseconds
     *                  since the Unix epoch
     * @param label     the type of measurement, such as ECG or oxygen saturation
     * @param data      the measurement data
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        // changed variable name to camelCase
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}
