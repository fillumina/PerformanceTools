package com.fillumina.performance.time.sample.strgen;

import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.CsvFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.infrastructure.AssertableConsumer;

/**
 * Print a {@link TimeSample} on the standard output {@link System#out}
 * as a CSV.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSampleCsvStringGenerator
        implements AssertableStringGenerator<TimeSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedSampleCsvStringGenerator INSTANCE =
            new SpeedSampleCsvStringGenerator();

    public static final AssertableConsumer<TimeSample> VIEWER =
            new AssertableViewer<>(INSTANCE);

    public static final AssertableConsumer<TimeSample> appendTo(
            Appendable appendable) {
        return new AssertableViewer<>(INSTANCE, appendable);
    }

    public SpeedSampleCsvStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, TimeSample speedSample)
            throws IOException {
        appendable.append(toString(speedSample));
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
    public String toString(TimeSample sample) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<TName, IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            IterationTime ti = entry.getValue();
            csv.append(ti.getTimeNs()).append(ti.getIterations());
        }
        return csv.toString();
    }
}
