package com.fillumina.performance.stats.viewer;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.TestPerformances;
import com.fillumina.performance.util.CsvFormatter;
import java.io.Serializable;
import java.util.Map;

/**
 * Produces a Comma Separated Value (CSV) line with the passed performances.
 *
 * @author Francesco Illuminati
 */
public final class StringCsvStatsViewer
        implements PerformanceConsumer<PerformanceStats>,
            PerformanceFormatter<PerformanceStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringCsvStatsViewer INSTANCE = new StringCsvStatsViewer();

    protected StringCsvStatsViewer() {}

    @Override
    public void consume(final String name, final PerformanceStats stats) {
        System.out.println(INSTANCE.toString(stats));
    }

    @Override
    public String toString(String title, PerformanceStats performance) {
        return toString(performance);
    }

    @Override
    public String toString(PerformanceStats performance) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<String, TestPerformances> e :
                performance.getTestPerformances().entrySet()) {
            TestPerformances tp = e.getValue();
            csv
                    .append(tp.getTotalTime())
                    .append(tp.getIterations());
        }
        return csv.toString();
    }
}
