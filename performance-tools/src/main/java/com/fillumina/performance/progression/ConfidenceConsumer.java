package com.fillumina.performance.progression;

/**
 * Consumes the confidence level of a statistics (created using ANOVA).
 *
 * @author Francesco Illuminati
 */
public interface ConfidenceConsumer {

    void consume(long iterations, long samples, double confidence);
}
