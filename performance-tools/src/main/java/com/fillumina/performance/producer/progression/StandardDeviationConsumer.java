package com.fillumina.performance.producer.progression;

/**
 *
 * @author Francesco Illuminati
 */
public interface StandardDeviationConsumer {

    void consume(long iterations, long samples, double stdDev);
}
