package com.fillumina.performance;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static com.fillumina.performance.TimeUnitHelper.*;

/**
 *
 * @author fra
 */
public class StringTablePresenter implements Presenter {
    private static final long serialVersionUID = 1L;

    private LoopPerformances performances;
    private String message;

    public StringTablePresenter() {
    }

    public StringTablePresenter(final LoopPerformances performances) {
        this.performances = performances;
    }

    public StringTablePresenter(final PerformanceTimer pt) {
        this.performances = pt.getLoopPerformances();
    }

    @Override
    public StringTablePresenter setPerformances(LoopPerformances performances) {
        this.performances = performances;
        return this;
    }

    @Override
    public StringTablePresenter setMessage(final String message) {
        this.message = message;
        return this;
    }

    public String toCsvString(final long executions) {
        StringBuilder buf = createStringBuilder();
        buf.append(String.format("%15d", executions));
        buf.append(CsvHelper.toCsvString(performances.getPercentageList()));
        return buf.toString();
    }

    @Override
    public void show() {
        getTable().println();
    }

    public OutputHolder getTable() {
        final TimeUnit unit = TimeUnitHelper
                .minTimeUnit(performances.getNanosecondsPerCycleList());
        return getTable(unit);
    }

    public OutputHolder getTable(final TimeUnit unit) {
        final StringBuilder buf = createStringBuilder();
        final int longer = getLongerMessageSize();

        int index = 0;
        for (final TestPerformances tp : performances.getTests()) {
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
        return new OutputHolder(buf.toString());
    }

    private void getTotalString(final StringBuilder buf, final int longer,
            final TimeUnit unit) {
        final long totalTime = getTotal();
        final double totalPerCycle = totalTime * 1.0D / performances.getIterations();
        buf.append(equilizeLength("", longer))
            .append("\t   * :\t")
            .append(formatUnit(totalPerCycle, unit));
    }

    private StringBuilder createStringBuilder() {
        final StringBuilder builder = new StringBuilder();
        builder.append('\n');
        if (message != null) {
            builder.append(message).append(' ');
        }
        builder
                .append("(")
                .append(String.format("%,d", performances.getIterations()))
                .append(" iterations)")
                .append('\n');
        return builder;
    }

    private int getLongerMessageSize() {
        int longer = 0;
        for (String msg: performances.getNameList()) {
            final int length = msg.length();
            if (length > longer) {
                longer = length;
            }
        }
        return longer;
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
        return Math.round(performances.getStatistics().sum());
    }

}
