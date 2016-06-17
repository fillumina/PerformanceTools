package com.fillumina.performance.speed.sample.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.TableFormatter;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.infrastructure.StringGenerator;

/**
 * Print a {@link PerformanceSample} on the standard output {@link System#out}
 * as a informative table.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleTableStringGenerator
        implements StringGenerator<PerformanceSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleTableStringGenerator INSTANCE =
            new SampleTableStringGenerator();

    public static final PerformanceConsumer<PerformanceSample> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    protected SampleTableStringGenerator() {}

    @Override
    public String toString(ComposedName title, PerformanceSample sample) {
        return TableFormatter.title(title.toString(), '=') + toString(sample);
    }

    @Override
    public String toString(PerformanceSample sample) {
        TableFormatter tf = new TableFormatter();
        for (Map.Entry<String,IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            String name = entry.getKey();
            IterationTime ti = entry.getValue();
            tf.cell(name)
                    .cell(ti.getTime(), " ns")
                    .cell(ti.getIterations())
                    .endl();
        }
        return tf.toString();
    }
}
