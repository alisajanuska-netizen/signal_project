package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class DataStorageTest {

    @Test
    void getRecordsReturnsRecordsInsideInclusiveTimeRange() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "ECG", 1000L);
        storage.addPatientData(1, 200.0, "ECG", 2000L);
        storage.addPatientData(1, 300.0, "ECG", 3000L);

        List<PatientRecord> records = storage.getRecords(1, 1000L, 2000L);

        assertEquals(2, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
        assertEquals(200.0, records.get(1).getMeasurementValue());
    }

    @Test
    void getRecordsReturnsEmptyListForUnknownPatient() {
        DataStorage storage = new DataStorage();

        List<PatientRecord> records = storage.getRecords(999, 1000L, 2000L);

        assertTrue(records.isEmpty());
    }
}
