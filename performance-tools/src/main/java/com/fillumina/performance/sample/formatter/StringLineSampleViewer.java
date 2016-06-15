package com.fillumina.performance.sample.formatter;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringFormatter;
import com.fillumina.performance.sample.IterationTime;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.Map;

/**
 * Print a {@link PerformanceSample} on the standard output {@link System#out}
 * as a informative line.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringLineSampleViewer
        implements StringFormatter<PerformanceSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringLineSampleViewer INSTANCE =
            new StringLineSampleViewer();

    public static final PerformanceConsumer<PerformanceSample> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public StringLineSampleViewer() {}

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
                    .append(ti.getTime()).append(" ns, ")
                    .append(iterations).append(" it")
                    .append("}");
        }
        return buf.toString();
    }
}
