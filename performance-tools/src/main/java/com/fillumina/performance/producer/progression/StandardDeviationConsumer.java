package com.fillumina.performance.producer.progression;

/**
 *
 * @author fra
 */
public interface StandardDeviationConsumer {

    void consume(long iterations, long samples, double stdDev);
}
