package com.fillumina.performance.stats;

import java.io.Serializable;

/**
 * A {@link PerformanceSampleConsumer} that does nothing. Useful to be passed
 * to methods that requires a consumer and doesn't accept {@code null}.
 *
 * @author Francesco Illuminati
 */
public final class NullPerformanceStatsConsumer
        implements PerformanceStatsConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullPerformanceStatsConsumer INSTANCE =
            new NullPerformanceStatsConsumer();

    private NullPerformanceStatsConsumer() {}

    @Override
    public void consume(final String message, final PerformanceStats stats) {
        // do nothing
    }

}
