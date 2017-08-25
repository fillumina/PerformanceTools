package com.fillumina.performance.time.sample.strgen;

import com.fillumina.performance.infrastructure.AssertableConsumer;
import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.time.sample.AverageTimeSample;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Print a {@link AverageTimeSample} on the standard output {@link System#out}
 * as a informative table.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSampleTableStringGenerator
        implements AssertableStringGenerator<AverageTimeSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedSampleTableStringGenerator INSTANCE =
            new SpeedSampleTableStringGenerator();

    public static final AssertableConsumer<AverageTimeSample> VIEWER =
            new AssertableViewer<>(AverageTimeSample.class, INSTANCE);

    public static final AssertableConsumer<AverageTimeSample> appendTo(
            Appendable appendable) {
        return new AssertableViewer<>(AverageTimeSample.class, INSTANCE, appendable);
    }

    protected SpeedSampleTableStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, AverageTimeSample speedSample)
            throws IOException {
        appendable.append(toString(speedSample));
    }

    @Override
    public String toString(AverageTimeSample sample) {
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
