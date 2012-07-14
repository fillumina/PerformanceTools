package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.utils.StringOutputHolder;
import com.fillumina.performance.producer.timer.TestPerformances;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static com.fillumina.performance.utils.TimeUnitHelper.*;
import java.io.Serializable;

/**
 *
 * @author fra
 */
public class StringTableViewer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final LoopPerformances loopPerformances;

    public static final PerformanceConsumer CONSUMER = new PerformanceConsumer() {
        @Override
        public void consume(final String message,
                final LoopPerformances loopPerformances) {
            new StringTableViewer(message, loopPerformances)
                    .getTable()
                    .println();
        }
    };

    public StringTableViewer(final LoopPerformances loopPerformances) {
        this(null, loopPerformances);
    }

    public StringTableViewer(final String message,
            final LoopPerformances loopPerformances) {
        this.message = message;
        this.loopPerformances = loopPerformances;
    }

    public StringOutputHolder getTable() {
        final TimeUnit unit =
                minTimeUnit(loopPerformances.getNanosecondsPerCycleList());
        return getTable(unit);
    }

    public StringOutputHolder getTable(final TimeUnit unit) {
        final StringBuilder buf = createStringBuilder();
        final int longer = getLongerMessageSize();

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
        getTotalString(buf, longer, unit);
        return new StringOutputHolder(buf.toString());
    }

    private void getTotalString(final StringBuilder buf, final int longer,
            final TimeUnit unit) {
        final long totalTime = getTotal();
        final double totalPerCycle =
                totalTime * 1.0D / loopPerformances.getIterations();
        buf.append(equilizeLength("", longer))
            .append("\t   * :\t")
            .append(formatUnit(totalPerCycle, unit));
    }

    private int getLongerMessageSize() {
        int longer = 0;
        for (String msg: loopPerformances.getNameList()) {
            final int length = msg.length();
            if (length > longer) {
                longer = length;
            }
        }
        return longer;
    }

    private StringBuilder createStringBuilder() {
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

    private long getTotal() {
        return Math.round(loopPerformances.getStatistics().sum());
    }

}
