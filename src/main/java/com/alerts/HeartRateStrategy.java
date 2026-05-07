package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {
    private AlertFactory factory = new AlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getAllRecords();

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("HeartRate")) {
                double value = record.getMeasurementValue();
                // Assumption: normal resting heart rate is between 50 and 120 bpm.
                if (value < 50 || value > 120) {
                    Alert alert = factory.createAlert("" + patient.getPatientId(),
                            "Abnormal Heart Rate", record.getTimestamp());
                    alerts.add(alert);
                }
            }
        }

        return alerts;
    }
}
