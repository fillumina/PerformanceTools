package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.io.Serializable;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class WrapperSpeedStatsTableStringGenerator
        implements StringGenerator<TimeStats>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Ratio DEFAULT_CONFIDENCE = Ratio.P_95;

    public static final WrapperSpeedStatsTableStringGenerator INSTANCE =
            new WrapperSpeedStatsTableStringGenerator();

    public static final PerformanceViewer<TimeStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<TimeStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    private final ParallelSingleTestSpeedStatsTableStringGenerator
            parallelSingleTestViewer;
    private final SingleTestSpeedStatsTableStringGenerator singleTestViewer;
    private final StringGenerator<TimeStats> multipleTestViewer;

    public WrapperSpeedStatsTableStringGenerator() {
        this(DEFAULT_CONFIDENCE);
    }

    public WrapperSpeedStatsTableStringGenerator(Ratio confidence) {
        parallelSingleTestViewer =
                new ParallelSingleTestSpeedStatsTableStringGenerator(confidence);
        singleTestViewer =
                new SingleTestSpeedStatsTableStringGenerator(confidence);
        multipleTestViewer =
                new SpeedStatsTableStringGenerator(confidence);
    }

    @Override
    public void appendTo(Appendable appendable, TimeStats speedStats)
            throws IOException {
        select(speedStats).appendTo(appendable, speedStats);
    }

    // TODO generalize this selection
    protected StringGenerator<TimeStats> select(TimeStats stats) {
        if (parallelSingleTestViewer.isCompatible(stats)) {
            return parallelSingleTestViewer;

        } else if (singleTestViewer.isCompatible(stats)) {
            return singleTestViewer;

        } else {
            return multipleTestViewer;

        }
    }
}
