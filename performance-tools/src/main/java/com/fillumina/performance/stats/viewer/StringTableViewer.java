package com.fillumina.performance.stats.viewer;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.TestPerformances;
import com.fillumina.performance.util.StringOutputHolder;
import static com.fillumina.performance.util.TimeUnitHelper.*;
import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Produces a human readable multi-line string of
 * the passed performances.
 *
 * @author Francesco Illuminati
 */
public final class StringTableViewer
        implements PerformanceStatsConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableViewer INSTANCE = new StringTableViewer();

    private StringTableViewer() {}

    @Override
    public void consume(final String message, final PerformanceStats stats) {
        INSTANCE.getTable(message, stats).print();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated and there is no title.
     */
    public StringOutputHolder getTable(final PerformanceStats stats) {
        return getTable(null, stats);
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    public StringOutputHolder getTable(final String message,
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
    public StringOutputHolder getTable(final String title,
            final PerformanceStats stats,
            final TimeUnit unit) {
        final StringBuilder buf = createHeader(title, stats);
        final int longer = getLongerMessageSize(stats);

        int index = 0;
        for (final TestPerformances tp : stats.getTestPerformances().values()) {
            final Measure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double confidence = tp.getConfidence();
            buf.append(equilizeLength(tp.getName(), longer))
                .append(String.format("\t%4d :", index))
                .append('\t')
                .append(formatUnit(elapsed.mean(), unit))
                .append(" ± ")
                .append(formatUnit(elapsed.marginOfError(confidence), unit))
                .append("  (confidence = ")
                .append(formatPercentage(confidence))
                .append(")")
                .append('\t')
                .append(tp.getPercentage().toString())
                .append('\n');

            index++;
        }
        getTotalString(stats, buf, longer, unit);
        buf.append('\n');
        return new StringOutputHolder(buf.toString());
    }

    private void getTotalString(final PerformanceStats stats,
            final StringBuilder buf, final int longer,
            final TimeUnit unit) {
        final long totalTime = getTotal(stats);
        buf.append(equilizeLength("", longer))
            .append("\t   * :\t")
            .append(formatUnit(totalTime, unit));
    }

    private int getLongerMessageSize(final PerformanceStats stats) {
        int longer = 0;
        for (String msg: stats.getTestPerformances().keySet()) {
            final int length = msg.length();
            if (length > longer) {
                longer = length;
            }
        }
        return longer;
    }

    private StringBuilder createHeader(final String message,
            final PerformanceStats stats) {
        final StringBuilder builder = new StringBuilder();
        builder.append('\n');
        if (message != null) {
            builder.append(message).append(' ');
        }
        builder.append('\n');
        return builder;
    }

    private static String equilizeLength(final String str, final int len) {
        final int length = str.length();
        if (length < len) {
            final char[] carray = new char[len - length];
            Arrays.fill(carray, ' ');
            return str + new String(carray);
        }
        return str;
    }

    private String formatPercentage(final double percentageValue) {
        return String.format("\t%10.2f %%", percentageValue);
    }

    private long getTotal(final PerformanceStats stats) {
        return Math.round(stats.getTotalTime());
    }
}
