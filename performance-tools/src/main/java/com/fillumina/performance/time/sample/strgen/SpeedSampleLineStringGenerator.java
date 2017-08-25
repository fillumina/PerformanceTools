package com.fillumina.performance.time.sample.strgen;

import com.fillumina.performance.infrastructure.AssertableConsumer;
import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.time.sample.AverageTimeSample;
import com.fillumina.performance.util.tname.TName;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Print a {@link AverageTimeSample} on the standard output {@link System#out}
 * as a informative line.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSampleLineStringGenerator
        implements AssertableStringGenerator<AverageTimeSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedSampleLineStringGenerator INSTANCE =
            new SpeedSampleLineStringGenerator();

    public static final AssertableConsumer<AverageTimeSample> VIEWER =
            new AssertableViewer<>(AverageTimeSample.class, INSTANCE);

    public static final AssertableConsumer<AverageTimeSample> appendTo(
            Appendable appendable) {
        return new AssertableViewer<>(AverageTimeSample.class, INSTANCE, appendable);
    }

    public SpeedSampleLineStringGenerator() {}

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
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<TName,IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            TName testName = entry.getKey();
            IterationTime ti = entry.getValue();
            long iterations = ti.getIterations();
            if (buf.length() != 0) {
                buf.append(", ");
            }
            buf.append('\'').append(testName).append("' {")
                    .append(ti.getTimeNs()).append(" ns, ")
                    .append(iterations).append(" it")
                    .append("}");
        }
        return buf.toString();
    }
}
