package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.IterationTimeAccumulator;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.sample.executor.PerformanceExecutor;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MockPerformanceExecutor implements PerformanceExecutor {

    private final Iterator<Double>[] iterators;

    /**
     * Creates a {@link PerformanceTimer}
     *
     * @param iterationsPerSample how many iterations
     * @param data array:
     *        <ol>
     *        <li>mean (double)
     *        <li>stdev (double)
     *        </ol>
     * @return the created {@link SpeedStats}
     */
    public static PerformanceTimer createPerformanceTimer(double[][] data) {
        return new DefaultPerformanceTimer(new MockPerformanceExecutor(data));
    }

    /**
     * Creates the {@link SpeedStats} based on normal distribution.
     *
     * @param iterationsPerSample how many iterations
     * @param data array:
     *        <ol>
     *        <li>mean (double)
     *        <li>stdev (double)
     *        </ol>
     * @return the created {@link SpeedStats}
     */
    @SuppressWarnings("unchecked")
    public MockPerformanceExecutor(double[][] data) {
        this.iterators = new Iterator[data.length];
        for (int i=0; i<iterators.length; i++) {
            double mean = data[i][0];
            double stdev = data[i][1];
            iterators[i] = new NormalDistributionMeasureBuilder(
                    mean, stdev, 0.1, 33).iterator();
        }
    }

    @Override
    public SpeedSample executeTests(LinkedHashMap<String, Testable> tests,
            int[] iterations) {
        int index = 0;
        LinkedHashMap<String, IterationTime> map = new LinkedHashMap<>();
        for (String name : tests.keySet()) {

            IterationTimeAccumulator it = new IterationTimeAccumulator();
            it.add((long)Math.floor(iterators[index].next()), 1);
            map.put(name, it);

            index++;
        }
        return new SpeedSample(tests.size(), map);
    }
}
