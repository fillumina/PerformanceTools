package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.speed.stats.SingleTestStats;
import com.fillumina.performance.speed.stats.SpeedRatio;
import com.fillumina.performance.speed.stats.SpeedStats;
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
    private static final Ratio CONFIDENCE = Ratio.P_95;

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

    protected SpeedStatsTableStringGenerator() {}

    @Override
    protected String getString(SpeedStats stats, IntervalUnit unit) {
        StringBuilder buf = new StringBuilder();

        TableFormatter header = creteHeader(stats);
        buf.append(header.toString());
        buf.append(System.lineSeparator());

        TableFormatter performanceTable = createPerformanceTable(stats, unit);
        buf.append(performanceTable.toString());

        buf.append(System.lineSeparator())
            .append("Ratio Matrix (confidence= ")
            .append(String.format(Locale.US,"%.3f %%",
                CONFIDENCE.getPercentage()))
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
        .param("Required measure confidence", CONFIDENCE)
        .param("Max ratio percentage margin",
                String.format(Locale.US, "%2.3f %%",
                        100 * stats.getMaximumPercentageMargin()))
        .param("ANOVA", stats.getAnova())
        .param("Minimum Tukey HSD accuracy for ratio",
                String.format(Locale.US, "%2.3f",
                        stats.getMinTukeyHsd()));
        return header;
    }

    private TableFormatter createPerformanceTable(final SpeedStats stats,
            final IntervalUnit unit) {
        TableFormatter performanceTable = new TableFormatter("  ");
        performanceTable
                .cell("idx")
                .cell("test name")
                .cell("ratio versus slower")
                .cell("time (samples used)")
                .cell("frequency")
                .cell("stdev")
                .cell("confidence")
                .cell("TukeyHSD")
                .endl();
        int index = 0;
        for (final SingleTestStats tp : stats.getPerformanceMap().values()) {
            final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double stdev = unit.convertFromBase(
                    elapsed.getUnbiasedStandardDeviation());
            String name = tp.getName();

            performanceTable
                    .cell(index)
                    .cell(name)
                    .cell(stats.getRatioWithSlowestTest(name, CONFIDENCE)
                            .toStringAsPercentage())
                    .cell(elapsed.toString(unit))
                    .cell(frequencyToString(
                            elapsed.getConfidenceInterval(CONFIDENCE)))
                    .cell(String.format(Locale.US,"%.3f", stdev))
                    .cell(String.format(Locale.US,"%.3f %%",
                            CONFIDENCE.getPercentage()))
                    .cell(String.format(Locale.US,"%.3f",
                            stats.getRatioWithSlowestTest(name, CONFIDENCE)
                            .getValue()))
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
                .endl();
        for (SpeedRatio pr : stats.getRatioList()) {
            double tukey = pr.getTukeyHSD();
            tukeyTable
                    .cell(pr.getTestName1())
                    .cell("vs")
                    .cell(pr.getTestName2())
                    .cell(pr.getRatio(CONFIDENCE).toAlternativeString())
                    .cell("(", pr.getInverseRatio(CONFIDENCE)
                            .toAlternativeString(), ")")
                    .cell(String.format(Locale.US,"%.3f", tukey));
            if (tukey > 0.6) {
                tukeyTable.cell("different");
            } else if (tukey < 0.4) {
                tukeyTable.cell("equals");
            } else {
                tukeyTable.cell("uncertain");
            }
            tukeyTable.endl();
        }
        return tukeyTable;
    }
}
