package com.fillumina.performance.progression;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class NullConfidenceConsumer
        implements ConfidenceConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullConfidenceConsumer INSTANCE =
            new NullConfidenceConsumer();

    private NullConfidenceConsumer() {}

    @Override
    public void consume(long iterations, long samples, double stdDev) {
        // do nothing
    }
}
