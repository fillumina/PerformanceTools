package com.fillumina.performance.time.sample.strgen;

import com.fillumina.performance.infrastructure.AssertableConsumer;
import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.time.sample.AverageTimeSample;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.formatter.CsvFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Print a {@link AverageTimeSample} on the standard output {@link System#out}
 * as a CSV.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSampleCsvStringGenerator
        implements AssertableStringGenerator<AverageTimeSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedSampleCsvStringGenerator INSTANCE =
            new SpeedSampleCsvStringGenerator();

    public static final AssertableConsumer<AverageTimeSample> VIEWER =
            new AssertableViewer<>(AverageTimeSample.class, INSTANCE);

    public static final AssertableConsumer<AverageTimeSample> appendTo(
            Appendable appendable) {
        return new AssertableViewer<>(AverageTimeSample.class, INSTANCE, appendable);
    }

    public SpeedSampleCsvStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, AverageTimeSample speedSample)
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
    public String toString(AverageTimeSample sample) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<TName, IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            IterationTime ti = entry.getValue();
            csv.append(ti.getTimeNs()).append(ti.getIterations());
        }
        return csv.toString();
    }
}
