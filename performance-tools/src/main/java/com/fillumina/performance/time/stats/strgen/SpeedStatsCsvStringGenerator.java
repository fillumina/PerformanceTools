package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.time.stats.SingleSpeedStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.CsvFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Produces a Comma Separated Value (CSV) line of statistics.
 *
 * @author Francesco Illuminati
 */
public final class SpeedStatsCsvStringGenerator
        implements StringGenerator<TimeStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedStatsCsvStringGenerator INSTANCE =
            new SpeedStatsCsvStringGenerator();

    public static final PerformanceViewer<TimeStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<TimeStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    protected SpeedStatsCsvStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, TimeStats speedStats)
            throws IOException {
        appendable.append(toString(speedStats));
    }

    @Override
    public String toString(TimeStats performance) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<TName, SingleSpeedStats> e :
                performance.getSingleStatsMap().entrySet()) {
            SingleSpeedStats tp = e.getValue();
            csv
                    .append(tp.getTotalTime())
                    .append(tp.getTotalIterations());
        }
        return csv.toString();
    }
}
