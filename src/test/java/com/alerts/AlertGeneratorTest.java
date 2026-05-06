package com.alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;

import org.junit.jupiter.api.Test;

class AlertGeneratorTest {

    @Test
    void testEmptyData() {
        Patient patient = new Patient(1);
        AlertGenerator generator = new AlertGenerator(new DataStorage());

        generator.evaluateData(patient);

        assertEquals(0, generator.getGeneratedAlerts().size());
    }

    @Test
    void testBloodPressureTrendIncrease() {
        Patient patient = new Patient(1);
        patient.addRecord(120.0, "SystolicPressure", 100);
        patient.addRecord(132.0, "SystolicPressure", 200);
        patient.addRecord(145.0, "SystolicPressure", 300);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        List<Alert> alerts = generator.getGeneratedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Blood Pressure Trend: SystolicPressure is increasing", alerts.get(0).getCondition());
    }

    @Test
    void testBloodPressureTrendDecrease() {
        Patient patient = new Patient(1);
        patient.addRecord(90.0, "DiastolicPressure", 100);
        patient.addRecord(78.0, "DiastolicPressure", 200);
        patient.addRecord(65.0, "DiastolicPressure", 300);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        List<Alert> alerts = generator.getGeneratedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Blood Pressure Trend: DiastolicPressure is decreasing", alerts.get(0).getCondition());
    }

    @Test
    void testNoTrendWhenChangeIsSmall() {
        Patient patient = new Patient(1);
        patient.addRecord(120.0, "SystolicPressure", 100);
        patient.addRecord(126.0, "SystolicPressure", 200);
        patient.addRecord(132.0, "SystolicPressure", 300);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        assertEquals(0, generator.getGeneratedAlerts().size());
    }

    @Test
    void testCriticalBloodPressure() {
        Patient patient = new Patient(1);
        patient.addRecord(181.0, "SystolicPressure", 100);
        patient.addRecord(89.0, "SystolicPressure", 200);
        patient.addRecord(121.0, "DiastolicPressure", 300);
        patient.addRecord(59.0, "DiastolicPressure", 400);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        assertEquals(4, generator.getGeneratedAlerts().size());
    }

    @Test
    void testBloodPressureBoundaryValues() {
        Patient patient = new Patient(1);
        patient.addRecord(180.0, "SystolicPressure", 100);
        patient.addRecord(90.0, "SystolicPressure", 200);
        patient.addRecord(120.0, "DiastolicPressure", 300);
        patient.addRecord(60.0, "DiastolicPressure", 400);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        assertEquals(0, generator.getGeneratedAlerts().size());
    }

    @Test
    void testLowOxygenSaturation() {
        Patient patient = new Patient(1);
        patient.addRecord(91.0, "Saturation", 100);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        List<Alert> alerts = generator.getGeneratedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Low Oxygen Saturation", alerts.get(0).getCondition());
    }

    @Test
    void testRapidOxygenDrop() {
        Patient patient = new Patient(1);
        patient.addRecord(98.0, "Saturation", 100);
        patient.addRecord(93.0, "Saturation", 100 + 9 * 60 * 1000);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        List<Alert> alerts = generator.getGeneratedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Rapid Oxygen Saturation Drop", alerts.get(0).getCondition());
    }

    @Test
    void testHypotensiveHypoxemia() {
        Patient patient = new Patient(1);
        patient.addRecord(88.0, "SystolicPressure", 100);
        patient.addRecord(91.0, "Saturation", 200);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        boolean found = false;
        List<Alert> alerts = generator.getGeneratedAlerts();
        for (Alert alert : alerts) {
            if (alert.getCondition().equals("Hypotensive Hypoxemia")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    void testEcgPeak() {
        Patient patient = new Patient(1);
        patient.addRecord(0.10, "ECG", 100);
        patient.addRecord(0.12, "ECG", 200);
        patient.addRecord(0.09, "ECG", 300);
        patient.addRecord(0.11, "ECG", 400);
        patient.addRecord(0.10, "ECG", 500);
        patient.addRecord(1.00, "ECG", 600);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        List<Alert> alerts = generator.getGeneratedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("ECG Abnormal Peak", alerts.get(0).getCondition());
    }

    @Test
    void testManualTriggeredAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(1.0, "Alert", 100);

        AlertGenerator generator = new AlertGenerator(new DataStorage());
        generator.evaluateData(patient);

        List<Alert> alerts = generator.getGeneratedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Manual Triggered Alert", alerts.get(0).getCondition());
    }
}
