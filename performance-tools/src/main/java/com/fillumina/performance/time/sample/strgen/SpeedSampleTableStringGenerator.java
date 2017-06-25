package com.fillumina.performance.time.sample.strgen;

import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.infrastructure.AssertableConsumer;

/**
 * Print a {@link TimeSample} on the standard output {@link System#out}
 * as a informative table.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSampleTableStringGenerator
        implements AssertableStringGenerator<TimeSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SpeedSampleTableStringGenerator INSTANCE =
            new SpeedSampleTableStringGenerator();

    public static final AssertableConsumer<TimeSample> VIEWER =
            new AssertableViewer<>(INSTANCE);

    public static final AssertableConsumer<TimeSample> appendTo(
            Appendable appendable) {
        return new AssertableViewer<>(INSTANCE, appendable);
    }

    protected SpeedSampleTableStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, TimeSample speedSample)
            throws IOException {
        appendable.append(toString(speedSample));
    }

    @Override
    public String toString(TimeSample sample) {
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
