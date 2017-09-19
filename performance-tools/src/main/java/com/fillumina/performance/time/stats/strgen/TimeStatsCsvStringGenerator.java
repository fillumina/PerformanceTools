package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.CsvFormatter;
import com.fillumina.performance.util.tname.TName;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Produces a Comma Separated Value (CSV) line of statistics.
 *
 * @author Francesco Illuminati
 */
public final class TimeStatsCsvStringGenerator<A extends TimeStats>
        implements StringGenerator<A>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final TimeStatsCsvStringGenerator<TimeStats> INSTANCE =
            new TimeStatsCsvStringGenerator<>();

    public static final Consumer<TimeStats> appendTo(
            Appendable appendable) {
        return new Viewer<>(INSTANCE, appendable);
    }

    @Override
    public void appendTo(Appendable appendable, A stats)
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
