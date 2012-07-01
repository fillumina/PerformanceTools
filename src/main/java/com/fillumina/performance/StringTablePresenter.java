package com.fillumina.performance;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static com.fillumina.performance.TimeUnitHelper.*;

/**
 *
 * @author fra
 */
public class StringTablePresenter extends AbstractPerformanceConsumer<StringTablePresenter> {
    private static final long serialVersionUID = 1L;

    @Override
    public void consume() {
        getTable().println();
    }

    public StringOutputHolder getTable() {
        final TimeUnit unit = TimeUnitHelper
                .minTimeUnit(getLoopPerformances().getNanosecondsPerCycleList());
        return getTable(unit);
    }

    public StringOutputHolder getTable(final TimeUnit unit) {
        final StringBuilder buf = createStringBuilder();
        final int longer = getLongerMessageSize();

        int index = 0;
        for (final TestPerformances tp : getLoopPerformances().getTests()) {
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
                totalTime * 1.0D / getLoopPerformances().getIterations();
        buf.append(equilizeLength("", longer))
            .append("\t   * :\t")
            .append(formatUnit(totalPerCycle, unit));
    }

    private int getLongerMessageSize() {
        int longer = 0;
        for (String msg: getLoopPerformances().getNameList()) {
            final int length = msg.length();
            if (length > longer) {
                longer = length;
            }
        }
        return longer;
    }

    protected StringBuilder createStringBuilder() {
        final StringBuilder builder = new StringBuilder();
        builder.append('\n');
        final String message = getMessage();
        if (message != null) {
            builder.append(message).append(' ');
        }
        builder.append("(").
                append(String.format("%,d", getLoopPerformances().getIterations())).
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
        return Math.round(getLoopPerformances().getStatistics().sum());
    }

}
