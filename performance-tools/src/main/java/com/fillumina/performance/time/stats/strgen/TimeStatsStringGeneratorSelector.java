package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.Selectable;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class TimeStatsStringGeneratorSelector
        implements StringGenerator<Stats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringGenerator<Stats> AVERAGE_TIME =
            new TimeStatsStringGeneratorSelector(Arrays.asList(
            AverageTimeStatsParallelTableStringGenerator.INSTANCE,
            AverageTimeStatsSingleTestStringGenerator.INSTANCE,
            AverageTimeStatsTableStringGenerator.INSTANCE
        ));

    public static final StringGenerator<Stats> THROUGHPUT =
            new TimeStatsStringGeneratorSelector(Arrays.asList(
            ThroughputStatsParallelTableStringGenerator.INSTANCE,
            ThroughputStatsSingleTestStringGenerator.INSTANCE,
            ThroughputStatsTableStringGenerator.INSTANCE
        ));

    public static final StringGenerator<Stats> ALL =
            new TimeStatsStringGeneratorSelector(Arrays.asList(
                AverageTimeStatsParallelTableStringGenerator.INSTANCE,
                AverageTimeStatsSingleTestStringGenerator.INSTANCE,
                AverageTimeStatsTableStringGenerator.INSTANCE,
                ThroughputStatsParallelTableStringGenerator.INSTANCE,
                ThroughputStatsSingleTestStringGenerator.INSTANCE,
                ThroughputStatsTableStringGenerator.INSTANCE
        ));

    public static final Viewer<Stats> VIEWER = new Viewer<>(ALL);

    public static final Consumer<Stats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
                new TimeStatsStringGeneratorSelector(getList(confidence)),
                appendable);
    }

    private final List<AbstractTimeStatsBaseStringGenerator> list;

    public TimeStatsStringGeneratorSelector(Ratio confidence) {
        this.list = getList(confidence);
    }

    public TimeStatsStringGeneratorSelector(
            List<AbstractTimeStatsBaseStringGenerator> list) {
        this.list = list;
    }

    @Override
    public void appendTo(Appendable appendable, Stats stats)
            throws IOException {
        select(stats)
                .appendTo(appendable, stats);
    }

    protected AbstractTimeStatsBaseStringGenerator select(Stats stats) {
        AbstractTimeStatsBaseStringGenerator selected =
                Selectable.select(stats, list);
        return selected;
    }

    private static List<AbstractTimeStatsBaseStringGenerator> getList(
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
