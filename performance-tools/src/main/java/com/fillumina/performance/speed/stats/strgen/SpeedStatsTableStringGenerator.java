package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.speed.stats.SingleSpeedStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Locale;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class SpeedStatsTableStringGenerator
        extends AbstractSpeedStatsStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final SpeedStatsTableStringGenerator SPEED_INSTANCE =
            new SpeedStatsTableStringGenerator();

    public static final PerformanceViewer<SpeedStats> SPEED_VIEWER =
            new PerformanceViewer<>(SPEED_INSTANCE);

    @SuppressWarnings("unchecked")
    public static final PerformanceConsumer<SpeedStats> getViewer() {
        return (PerformanceConsumer<SpeedStats>) SPEED_VIEWER;
    }

    public static final PerformanceConsumer<SpeedStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(SPEED_INSTANCE, appendable);
    }

    private final Ratio confidence;

    public SpeedStatsTableStringGenerator() {
        this(DEFAULT_CONFIDENCE);
    }

    public SpeedStatsTableStringGenerator(Ratio confidence) {
        this.confidence = confidence;
    }

    @Override
    protected String getString(SpeedStats stats, IntervalUnit unit) {
        StringBuilder buf = new StringBuilder();
        appendTitlePrefix(buf, stats);
        TableFormatter header = creteHeader(stats);
        buf.append(header.toString());
        buf.append(System.lineSeparator());

        TableFormatter performanceTable = createPerformanceTable(stats, unit);
        buf.append(performanceTable.toString());

        return buf.append(System.lineSeparator()).toString();
    }

    private TableFormatter creteHeader(final SpeedStats stats) {
        TableFormatter header = new TableFormatter("  ")
        .param("Test Time",
                IntervalUnit.getHelper().toPrettyString(stats.getTotalTimeNs()) )
        .param("Required measure confidence", confidence)
        .param("Max ratio percentage error",
                stats.getMaximumPercentageMargin(confidence).toString())
        .param("ANOVA", stats.getAnova());
        return header;
    }

    private TableFormatter createPerformanceTable(final SpeedStats stats,
            final IntervalUnit unit) {
        TableFormatter performanceTable = new TableFormatter("  ");
        performanceTable
                .cell("idx")
                .cell("name")
                .cell("samples")
                .cell("ratio vs slower")
                .cell("speed")
                .cell("stdev")
                .cell("frequency")
                .cell("confidence")
                .cell("TukeyHSD")
                .endl();
        int index = 0;
        for (final SingleSpeedStats tp : stats.getSingleStatsMap().values()) {
            final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double stdev = unit.convertFromBase(
                    elapsed.getUnbiasedStandardDeviation());
            TName name = tp.getName();

            performanceTable
                    .cell(index)
                    .cell(name.toString())
                    .cell(elapsed.getCount())
                    .cell(stats.getRatioWithSlowestTest(name, confidence)
                            .toStringAsPercentage())
                    .cell(elapsed.toStringForConfidenceWitoutSamples(
                            confidence, unit))
                    .cell(String.format(Locale.US,"%.3f", stdev))
                    .cell(frequencyToString(
                            elapsed.getConfidenceInterval(confidence)))
                    .cell(String.format(Locale.US,"%.3f %%",
                            confidence.getPercentage()))
                    .cell(String.format(Locale.US,"%.3f",
                            stats.getTukeyHsdComparedToSlowest(name)))
                    .endl();

            index++;
        }
        return performanceTable;
    }
}
