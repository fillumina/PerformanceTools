package com.fillumina.performance.stats.viewer;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.TestPerformances;
import com.fillumina.performance.util.StringOutputHolder;
import com.fillumina.performance.util.TableFormatter;
import com.fillumina.performance.util.TimeUnitFormatter;
import static com.fillumina.performance.util.TimeUnitFormatter.*;
import com.fillumina.performance.util.stats.OnlineMeasure;
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
        implements PerformanceStatsConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableStatsViewer INSTANCE = new StringTableStatsViewer();

    private StringTableStatsViewer() {}

    @Override
    public void consume(final String message, final PerformanceStats stats) {
        getTable(message, stats).print();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated and there is no title.
     */
    public static StringOutputHolder toStringOutput(final PerformanceStats stats) {
        return getTable(null, stats);
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    public static StringOutputHolder getTable(final String message,
            final PerformanceStats stats) {
        final Map<String, TestPerformances> testMap = stats.getTestPerformances();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (TestPerformances tp : testMap.values()) {
            times[counter] = tp.getElapsedNanosecondsPerCycle().mean();
            counter++;
        }
        final TimeUnit unit = minTimeUnit(times);
        return getTable(message, stats, unit);
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
    public static StringOutputHolder getTable(final String title,
            final PerformanceStats stats,
            final TimeUnit unit) {
        StringBuilder buf = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            buf.append(title)
                    .append('\n')
                    .append(TableFormatter.repeate('-', title.length()))
                    .append('\n');
        }

        buf.append("Confidence = ")
                .append(stats.getConfidence())
                .append('\n');
        buf.append("ANOVA = ")
                .append(stats.getAnova())
                .append('\n');
        buf.append("Minimum Tukey HSD = ")
                .append(stats.getMaxTukeyHsd())
                .append('\n');

        TableFormatter table = new TableFormatter("  ");
        int index = 0;
        for (final TestPerformances tp : stats.getTestPerformances().values()) {
            final OnlineMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double confidence = tp.getConfidence();
            table
                    .cell(index)
                    .cell(tp.getName())
                    .cell(elapsed.toStringForConfidence(confidence)+ " " +
                            TimeUnitFormatter.printSymbol(unit))
                    .cell(tp.getPercentage().toStringAsPercentage())
                    .cell("TukeyHSD = " + formatPercentage(tp.getTukeyHsd()))
                    .endl();

            index++;
        }
        buf.append(table.toString());
        return new StringOutputHolder(buf.toString());
    }

    private static String formatPercentage(final double percentageValue) {
        return String.format("%3.4f %%", percentageValue * 100.0);
    }
}
