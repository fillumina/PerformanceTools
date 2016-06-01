package com.fillumina.performance.sample.formatter;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.sample.IterationTime;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.TableFormatter;
import java.io.Serializable;
import java.util.Map;

/**
 * Print a {@link PerformanceSample} on the standard output {@link System#out}
 * as a informative table.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableSampleFormatter
        implements PerformanceFormatter<PerformanceSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableSampleFormatter INSTANCE =
            new StringTableSampleFormatter();

    public static final PerformanceConsumer<PerformanceSample> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    protected StringTableSampleFormatter() {}

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
