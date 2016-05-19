package com.fillumina.performance.stats.viewer;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.TestPerformances;
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
public final class StringTableStatsViewer
        implements PerformanceConsumer<PerformanceStats>,
            PerformanceFormatter<PerformanceStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableStatsViewer INSTANCE =
            new StringTableStatsViewer();

    protected StringTableStatsViewer() {}

    @Override
    public void consume(final String message, final PerformanceStats stats) {
        System.out.println(toString(message, stats));
    }

    @Override
    public String toString(String title, PerformanceStats stats) {
        return TableFormatter.title(title, '-') + toString(stats);
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    @Override
    public String toString(PerformanceStats stats) {
        final Map<String, TestPerformances> testMap = stats.getTestPerformances();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (TestPerformances tp : testMap.values()) {
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
        add(header, "Confidence", stats.getConfidence());
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
        for (final TestPerformances tp : stats.getTestPerformances().values()) {
            final Measure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double confidence = tp.getConfidence();
            table
                    .cell(index)
                    .cell(tp.getName())
                    .cell(elapsed.toStringForConfidence(confidence) + " " +
                            unitSymbol)
                    .cell("from " + tp.getOriginalTotalSamples() + " samples")
                    .cell(tp.getPercentage().toStringAsPercentage())
                    .cell("TukeyHSD = " + tp.getTukeyHsd())
                    .endl();

            index++;
        }
        buf.append(table.toString());
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
