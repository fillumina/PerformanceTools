package com.fillumina.performance.sample.viewer;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceSampleConsumer;
import com.fillumina.performance.stats.TimeIteration;
import com.fillumina.performance.util.TableFormatter;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableSampleViewer implements PerformanceSampleConsumer {

    public static final StringTableSampleViewer INSTANCE =
            new StringTableSampleViewer();

    @Override
    public void consume(String testName, PerformanceSample sample) {
        StringBuilder buf = new StringBuilder();
        if (testName != null && !testName.isEmpty()) {
            buf.append(testName)
                    .append('\n')
                    .append(TableFormatter.repeate('-', testName.length()))
                    .append('\n');
        }
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
        buf.append(tf.toString());
        System.out.println(buf.toString());
    }
}
