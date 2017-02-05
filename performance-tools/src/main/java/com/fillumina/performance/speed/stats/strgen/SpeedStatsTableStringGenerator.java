package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.stats.SpeedRatio;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class SpeedStatsTableStringGenerator
        implements StringGenerator<SpeedStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedStatsTableStringGenerator INSTANCE =
            new SpeedStatsTableStringGenerator();

    public static final PerformanceViewer<SpeedStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<SpeedStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    protected SpeedStatsTableStringGenerator() {}

    @Override
    public String toString(ComposedName name, SpeedStats stats) {
        StringBuilder buf = new StringBuilder();
        buf.append(System.lineSeparator());
        if (name != null && !name.isEmpty()) {
            buf.append(TableFormatter.title(name.toString(), '-'));
        }
        return buf.append(toString(stats)).toString();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    @Override
    public String toString(SpeedStats stats) {
        final Map<String, TestPerformance> testMap = stats.getPerformances();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (TestPerformance tp : testMap.values()) {
            times[counter] = tp.getElapsedNanosecondsPerCycle().getMean();
            counter++;
        }
        final IntervalUnit unit = IntervalUnit.FORMATTER.getMinUnit(times);
        return getTable(stats, unit);
    }

    private TableFormatter creteHeader(final SpeedStats stats) {
        TableFormatter header = new TableFormatter("  ")
        .param("Total Time",
                IntervalUnit.FORMATTER.toString(stats.getTotalTime()) )
        .param("Required measure confidence", "95 %")
        .param("Max ratio percentage margin",
                String.format("%2.3f %%",
                        100 * stats.getMaximumPercentageMargin()))
        .param("ANOVA", stats.getAnova())
        .param("Minimum Tukey HSD accuracy for ratio",
                String.format("%2.3f",
                        stats.getMinTukeyHsdEvaluationPercentage()));
        return header;
    }

    /**
     * Display a human readable text only multi line string with the
     * passed performances.
     *
     * @param title             The title of the table.
     * @param stats  The performances to display.
     * @param unit              The unit of time to use.
     * @return uses a {@link StringOutputHolder} for an easier manipulation
     *          using the
     *          <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *          fluent interface</a></i>.
     */
    public String getTable(final SpeedStats stats,
            final IntervalUnit unit) {
        final boolean singleTest = stats.getPerformances().size() == 1;
        StringBuilder buf = new StringBuilder();

        buf.append("\nPerformances:\n");

        if (singleTest) {
            buf.append(createSingleTestPerformance(stats, unit));
        } else {
            TableFormatter header = creteHeader(stats);
            buf.append(header.toString());

            TableFormatter performanceTable = createPerformanceTable(stats, unit);
            buf.append(performanceTable.toString());

            buf.append("\nRatio Matrix:").append(System.lineSeparator());
            TableFormatter tukeyTable = createTukeyTable(stats);
            buf.append(tukeyTable.toString());
        }

        return buf.append(System.lineSeparator()).toString();
    }

    private TableFormatter createPerformanceTable(final SpeedStats stats,
            final IntervalUnit unit) {
        TableFormatter performanceTable = new TableFormatter("  ");
        performanceTable
                .cell("idx")
                .cell("test name")
                .cell("time (samples used)")
                .cell("samples/it")
                .cell("ratio versus slower")
                .cell("stdev")
                .cell("confidence")
                .cell("TukeyHSD")
                .endl();
        int index = 0;
        for (final TestPerformance tp : stats.getPerformances().values()) {
            final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double stdev = unit.convertFromBase(
                    elapsed.getUnbiasedStandardDeviation());

            performanceTable
                    .cell(index)
                    .cell(tp.getName())
                    .cell(elapsed.toString(unit))
                    .cell(tp.getOriginalSamples(), "/",
                            tp.getIterationsPerSample())
                    .cell(tp.getRatio().toStringAsPercentage())
                    .cell(String.format("%.3f", stdev))
                    .cell(String.format("%.3f %%",
                            tp.getRatio().getConfidence() * 100.0))
                    .cell(String.format("%.3f", tp.getTukeyHsd()))
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
                .cell("confidence")
                .cell("tukeyHSD")
                .endl();
        for (SpeedRatio pr : stats.getRatioList()) {
            double tukey = pr.getTukeyHSD();
            tukeyTable
                    .cell(pr.getTestName1())
                    .cell("vs")
                    .cell(pr.getTestName2())
                    .cell(pr.getRatio().toAlternativeString())
                    .cell("(", pr.getInverseRatio().toAlternativeString(), ")")
                    .cell(String.format("%.3f %%",
                            pr.getRatio().getConfidence() * 100.0))
                    .cell(String.format("%.3f", tukey));
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

    private String createSingleTestPerformance(SpeedStats stats,
            IntervalUnit unit) {
        TestPerformance tp = stats.getPerformances().values().iterator().next();

        final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
        final double stdev = unit.convertFromBase(
                elapsed.getUnbiasedStandardDeviation());

        final double confidence = tp.getRatio().getConfidence();

        final double accuracy =
                elapsed.getMarginOfError(confidence) /
                elapsed.getMean();

        TableFormatter header = new TableFormatter("  ")
        .param("Total Time",
                IntervalUnit.FORMATTER.toString(stats.getTotalTime()) )
        .param("Required measure confidence", "95 %");

        TableFormatter performanceTable = new TableFormatter("  ");
        performanceTable
                .cell("time (samples used)")
                .cell("frequency")
                .cell("samples/it")
                .cell("stdev")
                .cell("accuracy")
                .endl()
                .cell(elapsed.toString(unit))
                .cell(frequencyToString(elapsed.getMean()))
                .cell(tp.getOriginalSamples(), "/", tp.getIterationsPerSample())
                .cell(String.format("%.6f", stdev))
                .cell(String.format("%.6f %%", accuracy * 100.0))
                .endl();

        return header.toString() + System.lineSeparator() +
                performanceTable.toString();
    }

    String frequencyToString(double value) {
        double freq = 1E9 / value;
        if (freq > 0.1) {
            return String.format("%,.6f op/s", freq);
        }
        freq *= 60;
        if (freq > 0.1) {
            return String.format("%,.6f op/m", freq);
        }
        freq *= 60;
        if (freq > 0.1) {
            return String.format("%,.6f op/h", freq);
        }
        freq *= 24;
        return String.format("%,.6f op/d", freq);
    }
}
