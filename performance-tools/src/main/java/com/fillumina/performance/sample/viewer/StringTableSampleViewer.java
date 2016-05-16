package com.fillumina.performance.sample.viewer;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.TimeIteration;
import com.fillumina.performance.util.TableFormatter;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableSampleViewer
        implements PerformanceConsumer<PerformanceSample>,
            PerformanceFormatter<PerformanceSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableSampleViewer INSTANCE =
            new StringTableSampleViewer();

    protected StringTableSampleViewer() {}

    @Override
    public void consume(String testName, PerformanceSample sample) {
        System.out.println(TableFormatter.title(testName, '-') +
                sample.toString());
    }

    @Override
    public String toString(String title, PerformanceSample sample) {
        return TableFormatter.title(title, '=') + toString(sample);
    }

    @Override
    public String toString(PerformanceSample sample) {
        TableFormatter tf = new TableFormatter();
        for (Map.Entry<String,TimeIteration> entry :
                sample.getTimeMap().entrySet()) {
            String name = entry.getKey();
            TimeIteration ti = entry.getValue();
            tf.cell(name)
                    .cell(ti.getTime())
                    .cell(ti.getIterations())
                    .endl();
        }
        return tf.toString();
    }
}
