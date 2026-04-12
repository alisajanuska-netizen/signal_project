package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;
import java.io.IOException;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        DataReader reader = new DataReader() {
            @Override
            public void readData(DataStorage dataStorage) throws IOException {
                dataStorage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
                dataStorage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);
            }
        };
        DataStorage storage = new DataStorage();
        try {
            reader.readData(storage);
        } catch (IOException e) {
            fail("Mock reader should not throw IOException");
        }

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }
}
