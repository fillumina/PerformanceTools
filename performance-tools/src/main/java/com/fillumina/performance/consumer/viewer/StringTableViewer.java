package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.util.StringOutputHolder;
import com.fillumina.performance.producer.TestPerformances;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static com.fillumina.performance.util.TimeUnitHelper.*;
import java.io.Serializable;

/**
 * Produces a human readable multi-line string of
 * the passed performances.
 *
 * @author Francesco Illuminati
 */
public final class StringTableViewer
        implements PerformanceConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableViewer INSTANCE = new StringTableViewer();

    private StringTableViewer() {}

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        INSTANCE.getTable(message, loopPerformances).print();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated and there is no title.
     */
    public StringOutputHolder getTable(final LoopPerformances loopPerformances) {
        return getTable(null, loopPerformances);
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    public StringOutputHolder getTable(final String message,
            final LoopPerformances loopPerformances) {
        final TimeUnit unit =
                minTimeUnit(loopPerformances.getNanosecondsPerCycleList());
        return getTable(message, loopPerformances, unit);
    }

    /**
     * Display a human readable text only multi line string with the
     * passed performances.
     *
     * @param title             The title of the table.
     * @param loopPerformances  The performances to display.
     * @param unit              The unit of time to use.
     * @return uses a {@link StringOutputHolder} for an easier manipulation
     *          using the
     *          <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *          fluent interface</a></i>.
     */
    public StringOutputHolder getTable(final String title,
            final LoopPerformances loopPerformances,
            final TimeUnit unit) {
        final StringBuilder buf = createHeader(title, loopPerformances);
        final int longer = getLongerMessageSize(loopPerformances);

        int index = 0;
        for (final TestPerformances tp : loopPerformances.getTests()) {
            buf.append(equilizeLength(tp.getName(), longer))
                .append(String.format("\t%4d :", index))
                .append('\t')
                .append(formatUnit(tp.getElapsedNanosecondsPerCycle(), unit))
                .append('\t')
                .append(formatPercentage(tp.getPercentage()))
                .append('\n');

            index++;
        }
        getTotalString(loopPerformances, buf, longer, unit);
        buf.append('\n');
        return new StringOutputHolder(buf.toString());
    }

    private void getTotalString(final LoopPerformances loopPerformances,
            final StringBuilder buf, final int longer,
            final TimeUnit unit) {
        final long totalTime = getTotal(loopPerformances);
        final double totalPerCycle =
                totalTime * 1.0D / loopPerformances.getIterations();
        buf.append(equilizeLength("", longer))
            .append("\t   * :\t")
            .append(formatUnit(totalPerCycle, unit));
    }

    private int getLongerMessageSize(final LoopPerformances loopPerformances) {
        int longer = 0;
        for (String msg: loopPerformances.getNameList()) {
            final int length = msg.length();
            if (length > longer) {
                longer = length;
            }
        }
        return longer;
    }

    private StringBuilder createHeader(final String message,
            final LoopPerformances loopPerformances) {
        final StringBuilder builder = new StringBuilder();
        builder.append('\n');
        if (message != null) {
            builder.append(message).append(' ');
        }
        builder.append("(").
                append(String.format("%,d", loopPerformances.getIterations())).
                append(" iterations)").append('\n');
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

    private long getTotal(final LoopPerformances loopPerformances) {
        return Math.round(loopPerformances.getStatistics().sum());
    }
}
