package com.fillumina.performance.speed.sample.strgen;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.Serializable;
import java.util.Map;

/**
 * Print a {@link SpeedSample} on the standard output {@link System#out}
 * as a informative table.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleTableStringGenerator
        implements StringGenerator<SpeedSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleTableStringGenerator INSTANCE =
            new SampleTableStringGenerator();

    public static final PerformanceConsumer<SpeedSample> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<SpeedSample> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    protected SampleTableStringGenerator() {}

    @Override
    public String toString(PHolder<SpeedSample> holder) {
        ComposedName title = holder.getName();
        SpeedSample sample = holder.getStats();
        return TableFormatter.title(title.toString(), '=') + toString(sample);
    }

    public String toString(SpeedSample sample) {
        TableFormatter tf = new TableFormatter();
        for (Map.Entry<String,IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            String name = entry.getKey();
            IterationTime ti = entry.getValue();
            tf.cell(name)
                    .cell(ti.getTimeNs(), " ns")
                    .cell(ti.getIterations(), " it")
                    .endl();
        }
        return tf.toString();
    }
}
