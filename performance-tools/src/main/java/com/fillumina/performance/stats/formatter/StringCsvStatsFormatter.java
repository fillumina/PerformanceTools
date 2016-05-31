package com.fillumina.performance.stats.formatter;

import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.TestPerformance;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.CsvFormatter;
import java.io.Serializable;
import java.util.Map;

/**
 * Produces a Comma Separated Value (CSV) line with the passed performances.
 *
 * @author Francesco Illuminati
 */
public final class StringCsvStatsFormatter
        implements PerformanceFormatter<PerformanceStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringCsvStatsFormatter INSTANCE =
            new StringCsvStatsFormatter();

    public static final PerformanceViewer<PerformanceStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    protected StringCsvStatsFormatter() {}

    @Override
    public String toString(ComposedName title, PerformanceStats performance) {
        return toString(performance);
    }

    @Override
    public String toString(PerformanceStats performance) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<String, TestPerformance> e :
                performance.getTestPerformances().entrySet()) {
            TestPerformance tp = e.getValue();
            csv
                    .append(tp.getTotalTime())
                    .append(tp.getIterations());
        }
        return csv.toString();
    }
}
