package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.CsvFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.infrastructure.AssertableConsumer;

/**
 * Produces a Comma Separated Value (CSV) line of statistics.
 *
 * @author Francesco Illuminati
 */
public final class TimeStatsCsvStringGenerator
        implements AssertableStringGenerator<TimeStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final TimeStatsCsvStringGenerator INSTANCE =
            new TimeStatsCsvStringGenerator();

    public static final AssertableConsumer<TimeStats> appendTo(
            Appendable appendable) {
        return new AssertableViewer<>(INSTANCE, appendable);
    }

    @Override
    public void appendTo(Appendable appendable, TimeStats stats)
            throws IOException {
        CsvFormatter csv = new CsvFormatter(appendable);
        for (Map.Entry<TName, SingleTimeStats> e :
                stats.getSingleStatsMap().entrySet()) {
            SingleTimeStats tp = e.getValue();
            csv
                    .append(tp.getTotalTime())
                    .append(tp.getTotalIterations());
        }
    }
}
