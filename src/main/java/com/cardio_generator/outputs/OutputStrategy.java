package com.cardio_generator.outputs;

/**
 * Defines a strategy for delivering generated health measurements.
 *
 * This abstraction allows the simulator to remain independent of the actual
 * output mechanism.
 * Implementations may print data to the console, write it to files, send it
 * over the network,
 * or handle it in any other suitable way.
 */
public interface OutputStrategy {
    /**
     * Emits one generated measurement.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time when the measurement was generated, in milliseconds
     *                  since the Unix epoch
     * @param label     the type of measurement, for example ECG or oxygen
     *                  saturation
     * @param data      the generated measurement data
     */
    void output(int patientId, long timestamp, String label, String data);
}
