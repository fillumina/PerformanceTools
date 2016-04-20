package com.fillumina.performance.stats.viewer;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.TestPerformances;
import com.fillumina.performance.util.StringOutputHolder;
import java.io.Serializable;
import java.util.Collection;

/**
 * Produces a Comma Separated Value (CSV) line with the passed performances.
 *
 * @author Francesco Illuminati
 */
public final class StringCsvViewer
        implements PerformanceStatsConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringCsvViewer INSTANCE = new StringCsvViewer();

    private StringCsvViewer() {}

    @Override
    public void consume(final String message, final PerformanceStats stats) {
        toCsvString(stats).print();
    }

    /**
     * The first column is the number of iterations performed to obtain the
     * statistics, the other columns are the performances expressed in
     * percentage relative to the slower (which is always 100%) in the same
     * order as returned by {@link PerformanceSample#getPercentage() }.
     *
     * @param stats
     * @return a formatted CSV line enclosed into a {@link StringOutputHolder}
     *          to allow easier manipulation using a
     *          <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *          fluent interface</a></i>.
     */
    public StringOutputHolder toCsvString(final PerformanceStats stats) {
        StringBuilder buf = new StringBuilder();
        appendCsvString(buf, stats.getTestPerformances().values());
        return new StringOutputHolder(buf.toString());
    }

    // TODO needs to print out interval of confidence too
    private static void appendCsvString(
            final StringBuilder buf,
            final Collection<TestPerformances> testPerformances) {
        boolean first = true;
        for (final TestPerformances tp : testPerformances) {
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(String.format("%.2f", tp.getPercentage().getValue()));
        }
    }
}
