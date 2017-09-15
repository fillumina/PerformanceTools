package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.infrastructure.AssertableConsumer;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.Selectable;
import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import com.fillumina.performance.util.StringGenerator;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class TimeStatsStringGeneratorSelector
        implements StringGenerator<TimeStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final TimeStatsStringGeneratorSelector AVERAGE_TIME =
            new TimeStatsStringGeneratorSelector(Arrays.asList(
            AverageTimeStatsParallelTableStringGenerator.INSTANCE,
            AverageTimeStatsSingleTestStringGenerator.INSTANCE,
            AverageTimeStatsTableStringGenerator.INSTANCE
        ));

    public static final TimeStatsStringGeneratorSelector THROUGHPUT =
            new TimeStatsStringGeneratorSelector(Arrays.asList(
            ThroughputStatsParallelTableStringGenerator.INSTANCE,
            ThroughputStatsSingleTestStringGenerator.INSTANCE,
            ThroughputStatsTableStringGenerator.INSTANCE
        ));

    public static final TimeStatsStringGeneratorSelector ALL =
            new TimeStatsStringGeneratorSelector(Arrays.asList(
                AverageTimeStatsParallelTableStringGenerator.INSTANCE,
                AverageTimeStatsSingleTestStringGenerator.INSTANCE,
                AverageTimeStatsTableStringGenerator.INSTANCE,
                ThroughputStatsParallelTableStringGenerator.INSTANCE,
                ThroughputStatsSingleTestStringGenerator.INSTANCE,
                ThroughputStatsTableStringGenerator.INSTANCE
        ));

    public static final AssertableViewer<TimeStats> VIEWER =
            new AssertableViewer<>(TimeStats.class, ALL);

    public static final AssertableConsumer<TimeStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new AssertableViewer<>(
                TimeStats.class,
                new TimeStatsStringGeneratorSelector(getList(confidence)),
                appendable);
    }

    private final List<AbstractTimeStatsBaseStringGenerator<?>> list;

    public TimeStatsStringGeneratorSelector(Ratio confidence) {
        this.list = getList(confidence);
    }

    public TimeStatsStringGeneratorSelector(
            List<AbstractTimeStatsBaseStringGenerator<?>> list) {
        this.list = list;
    }

    @Override
    public void appendTo(Appendable appendable, TimeStats assertable)
            throws IOException {
        select(assertable)
                .appendTo(appendable, assertable);
    }

    protected AbstractTimeStatsBaseStringGenerator<TimeStats> select(
            TimeStats stats) {
        @SuppressWarnings("unchecked")
        AbstractTimeStatsBaseStringGenerator<TimeStats> selected =
                (AbstractTimeStatsBaseStringGenerator<TimeStats>)
                Selectable.select(stats, list);
//        System.out.println("selected=" + selected.getClass().getCanonicalName());
        return selected;
    }

    private static List<AbstractTimeStatsBaseStringGenerator<?>> getList(
            Ratio confidence) {
        return Arrays.asList(
                new AverageTimeStatsParallelTableStringGenerator(confidence),
                new AverageTimeStatsSingleTestStringGenerator(confidence),
                new AverageTimeStatsTableStringGenerator(confidence),
                new ThroughputStatsParallelTableStringGenerator(confidence),
                new ThroughputStatsSingleTestStringGenerator(confidence),
                new ThroughputStatsTableStringGenerator(confidence));
    }
}
