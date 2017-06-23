package com.fillumina.performance.time.sample.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.time.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Print a {@link SpeedSample} on the standard output {@link System#out}
 * as a informative table.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSampleTableStringGenerator
        implements StringGenerator<SpeedSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedSampleTableStringGenerator INSTANCE =
            new SpeedSampleTableStringGenerator();

    public static final PerformanceConsumer<SpeedSample> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<SpeedSample> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    protected SpeedSampleTableStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, SpeedSample speedSample)
            throws IOException {
        appendable.append(toString(speedSample));
    }

    @Override
    public String toString(SpeedSample sample) {
        TableFormatter tf = new TableFormatter();
        for (Map.Entry<TName,IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            TName name = entry.getKey();
            IterationTime ti = entry.getValue();
            tf.cell(name)
                    .cell(ti.getTimeNs(), " ns")
                    .cell(ti.getIterations(), " it")
                    .endl();
        }
        return tf.toString();
    }
}
