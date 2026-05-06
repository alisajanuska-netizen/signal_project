package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * The class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a DataStorage instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private static final String SYSTOLIC = "SystolicPressure";
    private static final String DIASTOLIC = "DiastolicPressure";
    private static final String SATURATION = "Saturation";
    private static final String ECG = "ECG";
    private static final String MANUAL_ALERT = "Alert";
    private static final long TEN_MINUTES_MS = 10 * 60 * 1000L;
    private static final int ECG_WINDOW_SIZE = 5;
    private static final double ECG_PEAK_DELTA = 0.5;

    private DataStorage dataStorage;
    private final List<Alert> generatedAlerts;

    /**
     * Constructs an AlertGenerator with a specified DataStorage.
     * The DataStorage is used to retrieve patient data that this class will monitor
     * and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.generatedAlerts = new ArrayList<>();
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * triggerAlert method. This method should define the specific conditions under
     * which an
     * alert will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        if (patient == null) {
            return;
        }

        List<PatientRecord> records = sortedRecords(patient);
        if (records.isEmpty()) {
            return;
        }

        checkBloodPressureThresholds(patient, records);
        checkBloodPressureTrend(patient, records, SYSTOLIC);
        checkBloodPressureTrend(patient, records, DIASTOLIC);
        checkLowSaturation(patient, records);
        checkRapidSaturationDrop(patient, records);
        checkHypotensiveHypoxemia(patient, records);
        checkEcgPeak(patient, records);
        checkManualTriggeredAlerts(patient, records);
    }

    /**
     * Triggers an alert for the monitoring system. The method currently assumes
     * that the alert information is fully formed when passed as an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        generatedAlerts.add(alert);
    }

    public List<Alert> getGeneratedAlerts() {
        return new ArrayList<>(generatedAlerts);
    }

    private List<PatientRecord> sortedRecords(Patient patient) {
        List<PatientRecord> records = patient.getAllRecords();

        for (int i = 0; i < records.size() - 1; i++) {
            for (int j = i + 1; j < records.size(); j++) {
                if (records.get(i).getTimestamp() > records.get(j).getTimestamp()) {
                    PatientRecord temp = records.get(i);
                    records.set(i, records.get(j));
                    records.set(j, temp);
                }
            }
        }

        return records;
    }

    private void checkBloodPressureThresholds(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();

            if (isType(record, SYSTOLIC) && (value > 180 || value < 90)) {
                triggerAlert(patient, "Critical Blood Pressure: systolic out of range", record.getTimestamp());
            }

            if (isType(record, DIASTOLIC) && (value > 120 || value < 60)) {
                triggerAlert(patient, "Critical Blood Pressure: diastolic out of range", record.getTimestamp());
            }
        }
    }

    private void checkBloodPressureTrend(Patient patient, List<PatientRecord> records, String type) {
        List<PatientRecord> pressureRecords = recordsOfType(records, type);
        for (int i = 2; i < pressureRecords.size(); i++) {
            PatientRecord first = pressureRecords.get(i - 2);
            PatientRecord second = pressureRecords.get(i - 1);
            PatientRecord third = pressureRecords.get(i);

            double firstValue = first.getMeasurementValue();
            double secondValue = second.getMeasurementValue();
            double thirdValue = third.getMeasurementValue();
            double firstChange = secondValue - firstValue;
            double secondChange = thirdValue - secondValue;

            boolean increasing = firstChange > 10 && secondChange > 10;
            boolean decreasing = firstChange < -10 && secondChange < -10;

            if (increasing || decreasing) {
                String direction = increasing ? "increasing" : "decreasing";
                triggerAlert(patient, "Blood Pressure Trend: " + type + " is " + direction, third.getTimestamp());
            }
        }
    }

    private void checkLowSaturation(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = recordsOfType(records, SATURATION);
        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92) {
                triggerAlert(patient, "Low Oxygen Saturation", record.getTimestamp());
            }
        }
    }

    private void checkRapidSaturationDrop(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = recordsOfType(records, SATURATION);
        for (int i = 0; i < saturationRecords.size(); i++) {
            PatientRecord current = saturationRecords.get(i);
            for (int j = 0; j < i; j++) {
                PatientRecord previous = saturationRecords.get(j);
                long elapsed = current.getTimestamp() - previous.getTimestamp();
                if (elapsed <= TEN_MINUTES_MS && previous.getMeasurementValue() - current.getMeasurementValue() >= 5) {
                    triggerAlert(patient, "Rapid Oxygen Saturation Drop", current.getTimestamp());
                    break;
                }
            }
        }
    }

    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> lowSystolicRecords = new ArrayList<>();
        List<PatientRecord> systolicRecords = recordsOfType(records, SYSTOLIC);
        for (PatientRecord record : systolicRecords) {
            if (record.getMeasurementValue() < 90) {
                lowSystolicRecords.add(record);
            }
        }

        List<PatientRecord> lowSaturationRecords = new ArrayList<>();
        List<PatientRecord> saturationRecords = recordsOfType(records, SATURATION);
        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92) {
                lowSaturationRecords.add(record);
            }
        }

        for (PatientRecord systolic : lowSystolicRecords) {
            for (PatientRecord saturation : lowSaturationRecords) {
                long timeDifference = Math.abs(saturation.getTimestamp() - systolic.getTimestamp());
                if (timeDifference <= TEN_MINUTES_MS) {
                    long timestamp = Math.max(systolic.getTimestamp(), saturation.getTimestamp());
                    triggerAlert(patient, "Hypotensive Hypoxemia", timestamp);
                    break;
                }
            }
        }
    }

    private void checkEcgPeak(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = recordsOfType(records, ECG);
        for (int i = ECG_WINDOW_SIZE; i < ecgRecords.size(); i++) {
            double total = 0.0;
            for (int j = i - ECG_WINDOW_SIZE; j < i; j++) {
                total = total + ecgRecords.get(j).getMeasurementValue();
            }
            double average = total / ECG_WINDOW_SIZE;
            double currentValue = ecgRecords.get(i).getMeasurementValue();

            // Assumption: an ECG value more than 0.5 above the moving average is an abnormal peak.
            if (currentValue > average + ECG_PEAK_DELTA) {
                triggerAlert(patient, "ECG Abnormal Peak", ecgRecords.get(i).getTimestamp());
            }
        }
    }

    private void checkManualTriggeredAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> alertRecords = recordsOfType(records, MANUAL_ALERT);
        for (PatientRecord record : alertRecords) {
            if (record.getMeasurementValue() >= 1.0) {
                triggerAlert(patient, "Manual Triggered Alert", record.getTimestamp());
            }
        }
    }

    private List<PatientRecord> recordsOfType(List<PatientRecord> records, String type) {
        List<PatientRecord> matchingRecords = new ArrayList<>();
        for (PatientRecord record : records) {
            if (isType(record, type)) {
                matchingRecords.add(record);
            }
        }
        return matchingRecords;
    }

    private boolean isType(PatientRecord record, String type) {
        return type.equalsIgnoreCase(record.getRecordType());
    }

    private void triggerAlert(Patient patient, String condition, long timestamp) {
        triggerAlert(new Alert(Integer.toString(patient.getPatientId()), condition, timestamp));
    }
}
