package com.fillumina.performance.mock;

import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.IterationTimeAccumulator;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import java.util.Iterator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceExecutorMock implements PerformanceExecutor {

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
        return new DefaultPerformanceTimer(new PerformanceExecutorMock(data));
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
    public PerformanceExecutorMock(double[][] data) {
        this.iterators = new Iterator[data.length];
        for (int i=0; i<iterators.length; i++) {
            double mean = data[i][0];
            double stdev = data[i][1];
            iterators[i] = new NormalDistributionMeasureBuilder(
                    mean, stdev, 0.1, 33).iterator();
        }
    }

    @Override
    public SpeedSample executeIterations(LinkedMap<TName, Runnable> tests,
            int[] iterations) {
        int index = 0;
        LinkedMap<TName, IterationTime> map = new LinkedMap<>();
        for (TName name : tests.keySet()) {

            IterationTimeAccumulator it = new IterationTimeAccumulator();
            it.add((long)Math.floor(iterators[index].next()), 1);
            map.put(name, it);

            index++;
        }
        return new SpeedSample(map);
    }
}
