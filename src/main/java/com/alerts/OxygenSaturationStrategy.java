package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {
    private static final long TEN_MINUTES = 10 * 60 * 1000L;
    private BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getAllRecords();

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("Saturation") && record.getMeasurementValue() < 92) {
                Alert alert = factory.createAlert("" + patient.getPatientId(),
                        "Low Oxygen Saturation", record.getTimestamp());
                alerts.add(alert);
            }
        }

        for (int i = 0; i < records.size(); i++) {
            PatientRecord current = records.get(i);
            if (!current.getRecordType().equalsIgnoreCase("Saturation")) {
                continue;
            }

            for (int j = 0; j < i; j++) {
                PatientRecord previous = records.get(j);
                if (!previous.getRecordType().equalsIgnoreCase("Saturation")) {
                    continue;
                }

                long time = current.getTimestamp() - previous.getTimestamp();
                double drop = previous.getMeasurementValue() - current.getMeasurementValue();
                if (time <= TEN_MINUTES && drop >= 5) {
                    Alert alert = factory.createAlert("" + patient.getPatientId(),
                            "Rapid Oxygen Saturation Drop", current.getTimestamp());
                    alerts.add(alert);
                    break;
                }
            }
        }

        return alerts;
    }
}
