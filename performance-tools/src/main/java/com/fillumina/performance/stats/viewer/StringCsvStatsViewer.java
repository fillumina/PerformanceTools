package com.fillumina.performance.stats.viewer;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.TestPerformances;
import com.fillumina.performance.util.CsvFormatter;
import com.fillumina.performance.util.StringOutputHolder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Produces a Comma Separated Value (CSV) line with the passed performances.
 *
 * @author Francesco Illuminati
 */
public final class StringCsvStatsViewer
        implements PerformanceStatsConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringCsvStatsViewer INSTANCE = new StringCsvStatsViewer();

    private StringCsvStatsViewer() {}

    @Override
    public void consume(final String name, final PerformanceStats stats) {
        toStringOutput(stats).print();
    }

    public static List<String> testNames(final PerformanceStats stats) {
        return new ArrayList<>(stats.getTestPerformances().keySet());
    }

    /**
     * Columns (tests are in the same order they were inserted):
     * <ol>
     * <li>total time elapsed to perform the specified iterations
     * <li>iterations performed for this measurement
     * <li>... other tests ...
     * </ol>
     *
     * @param stats
     * @return a formatted CSV line enclosed into a {@link StringOutputHolder}
     *          to allow easier manipulation using a
     *          <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *          fluent interface</a></i>.
     */
    public static StringOutputHolder toStringOutput(final PerformanceStats stats) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<String, TestPerformances> e :
                stats.getTestPerformances().entrySet()) {
            TestPerformances tp = e.getValue();
            csv
                    .append(tp.getTotalTime())
                    .append(tp.getIterations());
        }
        return new StringOutputHolder(csv.toString());
    }
}
