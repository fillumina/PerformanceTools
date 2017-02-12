package com.fillumina.performance.speed.sample.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.SpeedSample;
import java.io.Serializable;
import java.util.Map;

/**
 * Print a {@link SpeedSample} on the standard output {@link System#out}
 * as a informative line.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleLineStringGenerator
        implements StringGenerator<SpeedSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleLineStringGenerator INSTANCE =
            new SampleLineStringGenerator();

    public static final PerformanceConsumer<SpeedSample> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<SpeedSample> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    public SampleLineStringGenerator() {}

    @Override
    public String toString(PerformanceHolder<SpeedSample> holder) {
        return toString(holder.getStats());
    }

    /**
     * Columns (tests are in the same order they were inserted):
     * <ol>
     * <li>total time elapsed to perform the specified iterations
     * <li>iterations performed for this measurement
     * <li>... other tests ...
     * </ol>
     */
    public String toString(SpeedSample sample) {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String,IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            String testName = entry.getKey();
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
