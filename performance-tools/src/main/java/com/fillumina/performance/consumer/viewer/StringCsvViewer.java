package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.util.StringOutputHolder;
import java.io.Serializable;
import java.util.Collection;

/**
 * Produces a Comma Separated Value (CSV) line with the passed performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public final class StringCsvViewer
        implements Serializable, PerformanceConsumer {
    private static final long serialVersionUID = 1L;

    public static final StringCsvViewer INSTANCE = new StringCsvViewer();

    private StringCsvViewer() {}

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        INSTANCE.toCsvString(loopPerformances).println();
    }

    /**
     * The first column is the number of iterations performed to obtain the
     * statistics, the other columns are the performances expressed in
     * percentage relative to the slower (which is always 100%) in the same
     * order as returned by {@link LoopPerformances#getPercentageList() }.
     *
     * @param loopPerformances
     * @return a formatted CSV line enclosed into a {@link StringOutputHolder}
     *          to allow easier manipulation using a <i>fluid interface</i>.
     */
    public StringOutputHolder toCsvString(
            final LoopPerformances loopPerformances) {
        StringBuilder buf = new StringBuilder();
        buf.append(String.format("%d, ", loopPerformances.getIterations()));
        appendCsvString(buf, loopPerformances.getPercentageList());
        return new StringOutputHolder(buf.toString());
    }

    private static void appendCsvString(
            final StringBuilder buf,
            final Collection<Float> values) {
        boolean first = true;
        for (final float d: values) {
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(String.format("%.2f", d));
        }
    }
}
