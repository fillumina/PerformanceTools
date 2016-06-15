package com.fillumina.performance.sample.formatter;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.sample.IterationTime;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.CsvFormatter;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.infrastructure.StringFormatter;

/**
 * Print a {@link PerformanceSample} on the standard output {@link System#out}
 * as a CSV.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringCsvSampleViewer
        implements PerformanceConsumer<PerformanceSample>,
            StringFormatter<PerformanceSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringCsvSampleViewer INSTANCE =
            new StringCsvSampleViewer();

    public static final PerformanceConsumer<PerformanceSample> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public StringCsvSampleViewer() {}

    @Override
    public void consume(ComposedName testName, PerformanceSample sample) {
        System.out.println(toString(sample));
    }

    @Override
    public String toString(ComposedName name, PerformanceSample sample) {
        return toString(sample);
    }

    /**
     * Columns (tests are in the same order they were inserted):
     * <ol>
     * <li>total time elapsed to perform the specified iterations
     * <li>iterations performed for this measurement
     * <li>... other tests ...
     * </ol>
     */
    @Override
    public String toString(PerformanceSample sample) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<String, IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            IterationTime ti = entry.getValue();
            csv.line(ti.getTime(), ti.getIterations());
        }
        return csv.toString();
    }
}
