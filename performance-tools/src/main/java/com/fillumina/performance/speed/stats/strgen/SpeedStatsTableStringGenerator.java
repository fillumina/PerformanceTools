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
    private static final Ratio DEFAULT_CONFIDENCE = Ratio.P_95;

    public static final SpeedStatsTableStringGenerator INSTANCE =
            new SpeedStatsTableStringGenerator();

    public static final PerformanceViewer<SpeedStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    @SuppressWarnings("unchecked")
    public static final PerformanceConsumer<SpeedStats> getViewer() {
        return (PerformanceConsumer<SpeedStats>) VIEWER;
    }

    public static final PerformanceConsumer<SpeedStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    private final Ratio confidence;

    public SpeedStatsTableStringGenerator() {
        this.confidence = DEFAULT_CONFIDENCE;
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

        buf.append(System.lineSeparator())
            .append("Ratio Matrix (confidence= ")
            .append(String.format(Locale.US,"%.3f %%",
                confidence.getPercentage()))
            .append("):")
            .append(System.lineSeparator());

        TableFormatter tukeyTable = createTukeyTable(stats);
        buf.append(tukeyTable.toString());

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
                .cell("test name")
                .cell("ratio vs slower")
                .cell("time (samples used)")
                .cell("frequency")
                .cell("stdev")
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
                    .cell(name.getLastName())
                    .cell(stats.getRatioWithSlowestTest(name, confidence)
                            .toStringAsPercentage())
                    .cell(elapsed.toString(unit))
                    .cell(frequencyToString(
                            elapsed.getConfidenceInterval(confidence)))
                    .cell(String.format(Locale.US,"%.3f", stdev))
                    .cell(String.format(Locale.US,"%.3f %%",
                            confidence.getPercentage()))
                    .cell(String.format(Locale.US,"%.3f",
                            stats.getTukeyHsdComparedToSlowest(name)))
                    .endl();

            index++;
        }
        return performanceTable;
    }

    private TableFormatter createTukeyTable(final SpeedStats stats) {
        TableFormatter tukeyTable = new TableFormatter("  ");
        tukeyTable
                .cell("test names").span(3)
                .cell("percentage")
                .cell("inverse")
                .cell("tukeyHSD")
                .cell("equality")
                .endl();
        TName slowestName = stats.getSlowestTestName();
        for (TName name : stats.getTestNames()) {
            if (!name.equals(slowestName)) {
                double tukey = stats.getTukeyHsdComparedToSlowest(name);
                tukeyTable
                        .cell(name.getLastName())
                        .cell("vs")
                        .cell(slowestName.getLastName())
                        .cell(stats.getRatioWithSlowestTest(name, confidence)
                                .toAlternativeString())
                        .cell("(", stats.getRatio(slowestName, name, confidence)
                                .toAlternativeString(), ")")
                        .cell(String.format(Locale.US,"%.3f", tukey));
                if (tukey > 0.7) {
                    tukeyTable.cell("different");
                } else if (tukey < 0.5) {
                    tukeyTable.cell("equals");
                } else {
                    tukeyTable.cell("uncertain");
                }
                tukeyTable.endl();
            }
        }
        return tukeyTable;
    }
}
