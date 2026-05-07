package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {
    private BloodPressureAlertFactory factory = new BloodPressureAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getAllRecords();

        checkCriticalValues(patient, records, alerts);
        checkTrend(patient, records, "SystolicPressure", alerts);
        checkTrend(patient, records, "DiastolicPressure", alerts);

        return alerts;
    }

    private void checkCriticalValues(Patient patient, List<PatientRecord> records, List<Alert> alerts) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();

            if (record.getRecordType().equalsIgnoreCase("SystolicPressure") && (value > 180 || value < 90)) {
                Alert alert = factory.createAlert("" + patient.getPatientId(),
                        "Critical Blood Pressure: systolic out of range", record.getTimestamp());
                alerts.add(alert);
            }

            if (record.getRecordType().equalsIgnoreCase("DiastolicPressure") && (value > 120 || value < 60)) {
                Alert alert = factory.createAlert("" + patient.getPatientId(),
                        "Critical Blood Pressure: diastolic out of range", record.getTimestamp());
                alerts.add(alert);
            }
        }
    }

    private void checkTrend(Patient patient, List<PatientRecord> records, String type, List<Alert> alerts) {
        List<PatientRecord> pressureRecords = new ArrayList<>();
        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase(type)) {
                pressureRecords.add(record);
            }
        }

        for (int i = 2; i < pressureRecords.size(); i++) {
            double first = pressureRecords.get(i - 2).getMeasurementValue();
            double second = pressureRecords.get(i - 1).getMeasurementValue();
            double third = pressureRecords.get(i).getMeasurementValue();

            double firstChange = second - first;
            double secondChange = third - second;

            if (firstChange > 10 && secondChange > 10) {
                Alert alert = factory.createAlert("" + patient.getPatientId(),
                        "Blood Pressure Trend: " + type + " is increasing",
                        pressureRecords.get(i).getTimestamp());
                alerts.add(alert);
            }

            if (firstChange < -10 && secondChange < -10) {
                Alert alert = factory.createAlert("" + patient.getPatientId(),
                        "Blood Pressure Trend: " + type + " is decreasing",
                        pressureRecords.get(i).getTimestamp());
                alerts.add(alert);
            }
        }
    }
}
