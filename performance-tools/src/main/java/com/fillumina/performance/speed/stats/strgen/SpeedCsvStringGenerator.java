package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.speed.stats.PerformanceStats;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.CsvFormatter;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.infrastructure.StringGenerator;

/**
 * Produces a Comma Separated Value (CSV) line of statistics.
 *
 * @author Francesco Illuminati
 */
public final class SpeedCsvStringGenerator
        implements StringGenerator<PerformanceStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedCsvStringGenerator INSTANCE =
            new SpeedCsvStringGenerator();

    public static final PerformanceViewer<PerformanceStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    protected SpeedCsvStringGenerator() {}

    @Override
    public String toString(ComposedName title, PerformanceStats performance) {
        return toString(performance);
    }

    @Override
    public String toString(PerformanceStats performance) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<String, TestPerformance> e :
                performance.getPerformances().entrySet()) {
            TestPerformance tp = e.getValue();
            csv
                    .append(tp.getTotalTime())
                    .append(tp.getIterations());
        }
        return csv.toString();
    }
}
