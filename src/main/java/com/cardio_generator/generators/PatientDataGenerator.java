package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines a generator that produces simulated measurements for a patient.
 * Implementations encapsulate one type of synthetic health signal and write
 * generated values through the provided OutputStrategy.
 */
public interface PatientDataGenerator {
    /**
     * Generates the next measurement for a patient and sends it to the configured
     * output.
     *
     * @param patientId      the identifier of the patient whose data should be
     *                       produced
     * 
     * @param outputStrategy the destination that receives the generated measurement
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
