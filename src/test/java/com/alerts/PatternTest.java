package com.alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;
import com.data_management.Patient;

import org.junit.jupiter.api.Test;

class PatternTest {

    @Test
    void testBloodPressureAlertFactory() {
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "Pressure Alert", 100);

        assertEquals("1", alert.getPatientId());
        assertEquals("Pressure Alert", alert.getCondition());
        assertEquals(100, alert.getTimestamp());
    }

    @Test
    void testBloodOxygenAlertFactory() {
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("2", "Oxygen Alert", 200);

        assertEquals("2", alert.getPatientId());
        assertEquals("Oxygen Alert", alert.getCondition());
        assertEquals(200, alert.getTimestamp());
    }

    @Test
    void testEcgAlertFactory() {
        ECGAlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("3", "ECG Alert", 300);

        assertEquals("3", alert.getPatientId());
        assertEquals("ECG Alert", alert.getCondition());
        assertEquals(300, alert.getTimestamp());
    }

    @Test
    void testBloodPressureStrategy() {
        Patient patient = new Patient(1);
        patient.addRecord(181.0, "SystolicPressure", 100);

        BloodPressureStrategy strategy = new BloodPressureStrategy();
        List<Alert> alerts = strategy.checkAlert(patient);

        assertEquals(1, alerts.size());
        assertEquals("Critical Blood Pressure: systolic out of range", alerts.get(0).getCondition());
    }

    @Test
    void testOxygenSaturationStrategy() {
        Patient patient = new Patient(1);
        patient.addRecord(91.0, "Saturation", 100);

        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();
        List<Alert> alerts = strategy.checkAlert(patient);

        assertEquals(1, alerts.size());
        assertEquals("Low Oxygen Saturation", alerts.get(0).getCondition());
    }

    @Test
    void testHeartRateStrategy() {
        Patient patient = new Patient(1);
        patient.addRecord(130.0, "HeartRate", 100);

        HeartRateStrategy strategy = new HeartRateStrategy();
        List<Alert> alerts = strategy.checkAlert(patient);

        assertEquals(1, alerts.size());
        assertEquals("Abnormal Heart Rate", alerts.get(0).getCondition());
    }

    @Test
    void testPriorityAlertDecorator() {
        Alert alert = new Alert("1", "Low Oxygen Saturation", 100);
        PriorityAlertDecorator decorator = new PriorityAlertDecorator(alert);

        assertEquals("HIGH PRIORITY: Low Oxygen Saturation", decorator.getCondition());
    }

    @Test
    void testRepeatedAlertDecorator() {
        Alert alert = new Alert("1", "Low Oxygen Saturation", 100);
        RepeatedAlertDecorator decorator = new RepeatedAlertDecorator(alert);

        assertEquals("REPEATED: Low Oxygen Saturation", decorator.getCondition());
    }

    @Test
    void testDataStorageSingleton() {
        DataStorage first = DataStorage.getInstance();
        DataStorage second = DataStorage.getInstance();

        assertSame(first, second);
    }

    @Test
    void testHealthDataSimulatorSingleton() {
        HealthDataSimulator first = HealthDataSimulator.getInstance();
        HealthDataSimulator second = HealthDataSimulator.getInstance();

        assertSame(first, second);
    }
}
