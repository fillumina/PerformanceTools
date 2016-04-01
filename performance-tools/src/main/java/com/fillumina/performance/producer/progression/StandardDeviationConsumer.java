package com.fillumina.performance.producer.progression;

/**
 *
 * @see <a href='https://en.wikipedia.org/wiki/Standard_error'>
 *  Wikipedia: Standard Error</a>
 * @author Francesco Illuminati
 */
public interface StandardDeviationConsumer {

    void consume(long iterations, long samples, double stdDev);
}
