package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class WrapperSpeedStatsTableStringGenerator
        implements StringGenerator<SpeedStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final WrapperSpeedStatsTableStringGenerator INSTANCE =
            new WrapperSpeedStatsTableStringGenerator();

    public static final PerformanceViewer<SpeedStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<SpeedStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    protected WrapperSpeedStatsTableStringGenerator() {}

    @Override
    public String toString(SpeedStats stats) {
        return select(stats).toString(stats);
    }

    @Override
    public String toString(ComposedName name, SpeedStats stats) {
        return select(stats).toString(name, stats);
    }

    protected StringGenerator<SpeedStats> select(SpeedStats stats) {
        if (ParallelSingleTestSpeedStatsTableStringGenerator.INSTANCE
                .isCompatible(stats)) {
            return ParallelSingleTestSpeedStatsTableStringGenerator.INSTANCE;

        } else if (SingleTestSpeedStatsTableStringGenerator.INSTANCE
                .isCompatible(stats)) {
            return SingleTestSpeedStatsTableStringGenerator.INSTANCE;

        } else {
            return SpeedStatsTableStringGenerator.INSTANCE;

        }
    }
}
