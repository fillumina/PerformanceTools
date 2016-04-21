package com.fillumina.performance.sample.viewer;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceSampleConsumer;
import com.fillumina.performance.stats.TimeIteration;
import com.fillumina.performance.util.CsvFormatter;
import com.fillumina.performance.util.StringOutputHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringCsvSampleViewer implements PerformanceSampleConsumer {

    public static final StringCsvSampleViewer INSTANCE =
            new StringCsvSampleViewer();

    @Override
    public void consume(String testName, PerformanceSample sample) {
        toCsvString(sample).print();
    }

    public static List<String> testNames(final PerformanceSample sample) {
        return new ArrayList<>(sample.getTimeMap().keySet());
    }

    /**
     * Columns (tests are in the same order they were inserted):
     * <ol>
     * <li>total time elapsed to perform the specified iterations
     * <li>iterations performed for this measurement
     * <li>... other tests ...
     * </ol>
     */
    public static StringOutputHolder toCsvString(PerformanceSample sample) {
        CsvFormatter csv = new CsvFormatter();
        for (Map.Entry<String,TimeIteration> entry :
                sample.getTimeMap().entrySet()) {
            TimeIteration ti = entry.getValue();
            csv.append(ti.getTime())
                    .append(ti.getIterations());
        }
        return new StringOutputHolder(csv.toString());
    }
}
