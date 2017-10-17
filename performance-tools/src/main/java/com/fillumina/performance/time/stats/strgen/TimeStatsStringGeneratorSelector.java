package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.time.stats.TimeStats;
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
public final class TimeStatsStringGeneratorSelector<T extends TimeStats>
        implements StringGenerator<T>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringGenerator<AverageTimeStats> AVERAGE_TIME =
            new TimeStatsStringGeneratorSelector<>(Arrays.asList(
            AverageTimeStatsParallelTableStringGenerator.INSTANCE,
            AverageTimeStatsSingleTestStringGenerator.INSTANCE,
            AverageTimeStatsTableStringGenerator.INSTANCE
        ));

    public static final StringGenerator<ThroughputStats> THROUGHPUT =
            new TimeStatsStringGeneratorSelector<>(Arrays.asList(
            ThroughputStatsParallelTableStringGenerator.INSTANCE,
            ThroughputStatsSingleTestStringGenerator.INSTANCE,
            ThroughputStatsTableStringGenerator.INSTANCE
        ));

    public static final StringGenerator<TimeStats> ALL =
            new TimeStatsStringGeneratorSelector<>(Arrays.asList(
                AverageTimeStatsParallelTableStringGenerator.INSTANCE,
                AverageTimeStatsSingleTestStringGenerator.INSTANCE,
                AverageTimeStatsTableStringGenerator.INSTANCE,
                ThroughputStatsParallelTableStringGenerator.INSTANCE,
                ThroughputStatsSingleTestStringGenerator.INSTANCE,
                ThroughputStatsTableStringGenerator.INSTANCE
        ));

    public static final Viewer<TimeStats> VIEWER = new Viewer<>(ALL);

    public static final Consumer<TimeStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
                new TimeStatsStringGeneratorSelector<>(getList(confidence)),
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
