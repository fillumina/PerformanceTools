package com.fillumina.performance.speed.sample.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.CsvFormatter;
import java.io.Serializable;
import java.util.Map;

/**
 * Print a {@link SpeedSample} on the standard output {@link System#out}
 * as a CSV.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleCsvStringGenerator
        implements StringGenerator<SpeedSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleCsvStringGenerator INSTANCE =
            new SampleCsvStringGenerator();

    public static final PerformanceConsumer<SpeedSample> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<SpeedSample> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    public SampleCsvStringGenerator() {}

    @Override
    public String toString(ComposedName name, SpeedSample sample) {
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
    public String toString(SpeedSample sample) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<String, IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            IterationTime ti = entry.getValue();
            csv.append(ti.getTime()).append(ti.getIterations());
        }
        return csv.toString();
    }
}
