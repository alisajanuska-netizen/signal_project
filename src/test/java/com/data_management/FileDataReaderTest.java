package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileDataReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void readsHealthDataSimulatorOutputFiles() throws IOException {
        Files.write(tempDir.resolve("Saturation.txt"), Arrays.asList(
                "Patient ID: 1, Timestamp: 1000, Label: Saturation, Data: 91%",
                "Patient ID: 2, Timestamp: 1001, Label: ECG, Data: 0.42"));

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir).readData(storage);

        assertEquals(1, storage.getRecords(1, 0L, 2000L).size());
        assertEquals(91.0, storage.getRecords(1, 0L, 2000L).get(0).getMeasurementValue());
        assertEquals("Saturation", storage.getRecords(1, 0L, 2000L).get(0).getRecordType());
        assertEquals(0.42, storage.getRecords(2, 0L, 2000L).get(0).getMeasurementValue());
    }

    @Test
    void convertsTriggeredSimulatorAlertToNumericRecord() throws IOException {
        Files.write(tempDir.resolve("Alert.txt"), Arrays.asList(
                "Patient ID: 3, Timestamp: 1000, Label: Alert, Data: triggered",
                "Patient ID: 3, Timestamp: 1001, Label: Alert, Data: resolved"));

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir).readData(storage);

        List<PatientRecord> records = storage.getRecords(3, 0L, 2000L);
        assertEquals(2, records.size());
        assertEquals(1.0, records.get(0).getMeasurementValue());
        assertEquals(0.0, records.get(1).getMeasurementValue());
    }

    @Test
    void skipsMissingAndInvalidRecords() throws IOException {
        Files.write(tempDir.resolve("ECG.txt"), Arrays.asList(
                "not a simulator line",
                "Patient ID: bad, Timestamp: 1000, Label: ECG, Data: 0.5",
                "Patient ID: 1, Timestamp: 1000, Label: ECG, Data: missing"));

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir).readData(storage);

        assertEquals(0, storage.getRecords(1, 0L, 2000L).size());
    }
}
