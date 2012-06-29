package com.fillumina.performance;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static com.fillumina.performance.TimeUnitHelper.*;

/**
 *
 * @author fra
 */
public class Presenter implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LoopPerformances performances;
    private String message;

    public Presenter(final LoopPerformances performances) {
        this.performances = performances;
    }

    public Presenter(final PerformanceTimer pt) {
        this.performances = pt.getLoopPerformances();
    }

    public Presenter addMessage(final String message) {
        this.message = message;
        return this;
    }

    // TODO: automatize time unit
    public OutputHolder getComparison(final TimeUnit unit) {
        final StringBuilder buf = createStringBuilder();
        final int longer = getLongerMessageSize();
        for (final TestPerformances tp : performances.getTests()) {
            buf.append(equilizeLength(tp.getName(), longer))
                    .append(" :\t")
                    .append(formatUnit(tp.getElapsedNanoseconds(), unit))
                    .append("\t")
                    .append(formatPercentage(tp.getPercentage()))
                    .append("\n");
        }
        return new OutputHolder(buf.toString());
    }

    public OutputHolder getIncrements(final TimeUnit unit) {
        final StringBuilder buf = createStringBuilder();
        final long totalTime = getTotal();
        final int longer = getLongerMessageSize();

        int index = 0;
        for (final TestPerformances tp : performances.getTests()) {
            buf.append(equilizeLength(tp.getName(), longer))
                .append(String.format("\t%4d :", index))
                .append('\t')
                .append(formatUnit(tp.getElapsedNanoseconds(), unit))
                .append('\t')
                .append(formatPercentage(tp.getPercentage()))
                .append('\n');

            index++;
        }
        buf.append(equilizeLength("*", longer))
            .append("     :\t")
            .append(formatUnit(
                    totalTime * 1.0D / performances.getIterations(), unit));
        return new OutputHolder(buf.toString());
    }

    private StringBuilder createStringBuilder() {
        final StringBuilder builder = new StringBuilder();
        if (message != null) {
            builder.append(message).append('\n');
        }
        return builder;
    }

    public String toCsvString(final long executions) {
        StringBuilder buf = createStringBuilder();
        buf.append(String.format("%15d", executions));
        buf.append(CsvHelper.toCsvString(performances.getPercentageList()));
        return buf.toString();
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
