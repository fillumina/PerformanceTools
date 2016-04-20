package com.fillumina.performance.sample;

import java.io.Serializable;

/**
 * A {@link PerformanceSampleConsumer} that does nothing. Useful to be passed
 * to methods that requires a consumer and doesn't accept {@code null}.
 *
 * @author Francesco Illuminati
 */
public final class NullPerformanceSampleConsumer
        implements PerformanceSampleConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullPerformanceSampleConsumer INSTANCE =
            new NullPerformanceSampleConsumer();

    private NullPerformanceSampleConsumer() {}

    @Override
    public void consume(final String message, final PerformanceSample sample) {
        // do nothing
    }

}
