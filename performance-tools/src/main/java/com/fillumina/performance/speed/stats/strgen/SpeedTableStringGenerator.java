package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.stats.SpeedRatio;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.TableFormatter;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.util.unit.DimensionalMeasure;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class SpeedTableStringGenerator
        implements StringGenerator<SpeedStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedTableStringGenerator INSTANCE =
            new SpeedTableStringGenerator();

    public static final PerformanceViewer<SpeedStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    protected SpeedTableStringGenerator() {}

    @Override
    public String toString(ComposedName name, SpeedStats stats) {
        StringBuilder buf = new StringBuilder();
        if (!name.isEmpty()) {
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
        TableFormatter header = new TableFormatter("  ");
        add(header, "Total Time",
                IntervalUnit.FORMATTER.toString(stats.getTotalTime()));
        add(header, "Required measure confidence", "95 %");
        add(header, "Max ratio percentage margin",
                String.format("%2.3f %%",
                        100 * stats.getMaximumPercentageMargin()));
        add(header, "Statistical significance matrix prob",
                String.format("%2.3f",
                        stats.getStatisticalSignificanceMatrixProbability(0.9)));
        add(header, "ANOVA", stats.getAnova());
        add(header, "Minimum Tukey HSD accuracy for ratio",
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
        StringBuilder buf = new StringBuilder();

        TableFormatter header = creteHeader(stats);
        buf.append(header.toString());

        buf.append("\nPerformances:\n");
        TableFormatter performanceTable = createPerformanceTable(stats, unit);
        buf.append(performanceTable.toString());

        if (stats.getPerformances().size() > 1) {
            buf.append("\nRatio Matrix:").append(System.lineSeparator());
            TableFormatter tukeyTable = createTukeyTable(stats);
            buf.append(tukeyTable.toString());
        }

        return buf.append(System.lineSeparator()).toString();
    }

    private TableFormatter createPerformanceTable(final SpeedStats stats,
            final IntervalUnit unit) {
        TableFormatter performanceTable = new TableFormatter("  ");
        int index = 0;
        for (final TestPerformance tp : stats.getPerformances().values()) {
            final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double stdev = unit.convertFromBase(
                    elapsed.getUnbiasedStandardDeviation());

            performanceTable
                    .cell(index)
                    .cell(tp.getName())
                    .cell("stdev = ", String.format("%.3f", stdev))
                    .cell(elapsed.toString(unit))
                    .cell(tp.getOriginalSamples(), "/",
                            tp.getIterationsPerSample(), " sample/it")
                    .cell(tp.getRatio().toStringAsPercentageWithConfidence())
                    .cell("TukeyHSD = " + tp.getTukeyHsd())
                    .endl();

            index++;
        }
        return performanceTable;
    }

    private TableFormatter createTukeyTable(final SpeedStats stats) {
        TableFormatter tukeyTable = new TableFormatter("  ");
        for (SpeedRatio pr : stats.getRatioList()) {
            double tukey = pr.getTukeyHSD();
            tukeyTable
                    .cell(pr.getTestName1())
                    .cell("vs")
                    .cell(pr.getTestName2())
                    .cell(pr.getRatio().toAlternativeString())
                    .cell(pr.getInverseRatio().toAlternativeString())
                    .cell(String.format("confidence = %.3f %%",
                            pr.getRatio().getConfidence() * 100.0))
                    .cell("tukeyHSD = ", tukey);
            if (tukey > 0.8) {
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

    private void add(TableFormatter tf, String message, Object... values) {
        if (values[0] != null) {
            String msg = values[0].toString() +
                    ((values.length == 1) ? "" : " " + values[1].toString());
            tf.cell(message).cell("=").cell(msg).endl();
        }
    }
}
