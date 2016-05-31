package com.fillumina.performance.stats.formatter;

import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.TestPerformance;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.StringOutputHolder;
import com.fillumina.performance.util.TableFormatter;
import com.fillumina.performance.util.TimeUnitFormatter;
import static com.fillumina.performance.util.TimeUnitFormatter.*;
import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Produces a human readable multi-line string of
 * the passed performances.
 *
 * @author Francesco Illuminati
 */
public final class StringTableStatsFormatter
        implements PerformanceFormatter<PerformanceStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableStatsFormatter INSTANCE =
            new StringTableStatsFormatter();

    public static final PerformanceViewer<PerformanceStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    protected StringTableStatsFormatter() {}

    @Override
    public String toString(ComposedName name, PerformanceStats stats) {
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
    public String toString(PerformanceStats stats) {
        final Map<String, TestPerformance> testMap = stats.getTestPerformances();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (TestPerformance tp : testMap.values()) {
            times[counter] = tp.getElapsedNanosecondsPerCycle().getMean();
            counter++;
        }
        final TimeUnit unit = minTimeUnit(times);
        return getTable(stats, unit);
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
    public String getTable(final PerformanceStats stats,
            final TimeUnit unit) {
        String unitSymbol = TimeUnitFormatter.printSymbol(unit);
        StringBuilder buf = new StringBuilder();
        TableFormatter header = new TableFormatter("  ");

        add(header, "Rejection message", stats.getMessage());
        add(header, "Confidence",
                String.format("%.2f %%",stats.getConfidence() * 100));
        add(header, "Max ratio percentage margin",
                stats.getMaximumPercentageMargin());
        add(header, "Statistical significance matrix prob",
                stats.getStatisticalSignificanceMatrixProbability());
        add(header, "ANOVA", stats.getAnova());
        add(header, "Minimum Tukey HSD accuracy",
                stats.getMinTukeyHsdEvaluationPercentage());

        buf.append(header.toString());

        TableFormatter table = new TableFormatter("  ");
        int index = 0;
        for (final TestPerformance tp : stats.getTestPerformances().values()) {
            final Measure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double confidence = tp.getConfidence();
            final double stdev = tp.getElapsedNanosecondsPerCycle()
                            .getUnbiasedStandardDeviation();
            table
                    .cell(index)
                    .cell(tp.getName())
                    .cell(elapsed.toStringForConfidence(confidence) + " " +
                            unitSymbol)
                    .cell("stdev = " + String.format("%.3f", stdev) +
                            " " + unitSymbol)
                    .cell("from " + tp.getOriginalTotalSamples() + " samples")
                    .cell(tp.getPercentage().toStringAsPercentage())
                    .cell("TukeyHSD = " + tp.getTukeyHsd())
                    .endl();

            index++;
        }
        buf.append(table.toString());
        if (index > 1) {
            buf.append("Tukey HSD Matrix:").append(System.lineSeparator());
            TableFormatter tukeyTable = new TableFormatter("  ");
            double tukey;
            for (int i=0; i<index; i++) {
                for (int j=i+1; j<index; j++) {
                    tukey = stats.getTukeyKramerHsdConfidenceProbability(i,j);
                    tukeyTable
                            .cell(stats.getName(i))
                            .cell("vs")
                            .cell(stats.getName(j))
                            .cell(tukey);
                    if (tukey > 0.8) {
                        tukeyTable.cell("different");
                    } else if (tukey < 0.4) {
                        tukeyTable.cell("equals");
                    } else {
                        tukeyTable.cell("uncertain");
                    }
                    tukeyTable.endl();
                }
            }
            buf.append(tukeyTable.toString());
        }
        return buf.toString();
    }

    private void add(TableFormatter tf, String message, Object... values) {
        if (values[0] != null) {
            String msg = values[0].toString() +
                    ((values.length == 1) ? "" : " " + values[1].toString());
            tf.cell(message).cell("=").cell(msg).endl();
        }
    }
}
