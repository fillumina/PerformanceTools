package com.fillumina.performance;

import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.IterationTimeAccumulator;
import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.stats.SpeedSampleCollector;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati
 */
public class FakePerformanceCreator {

    /**
     * Creates the {@link SpeedStats} based on normal distribution.
     *
     * @param iterationsPerSample how many iterations
     * @param data array of quadruplets
     *        <ol>
     *        <li>name (String)
     *        <li>mean (double)
     *        <li>stdev (double)
     *        <li>number of samples (int)
     *        </ol>
     * @return the created {@link SpeedStats}
     */
    public static SpeedStats createPerformanceStats(
            final long iterationsPerSample,
            final Object[][] data) {

        @SuppressWarnings("unchecked")
        Iterator<Double>[] iterators = new Iterator[data.length];
        for (int i=0; i<iterators.length; i++) {
            double mean = (double) data[i][1];
            double stdev = (double) data[i][2];
            int minSamples = (int) data[i][3];
            iterators[i] = new NormalDistributionMeasureBuilder(
                    mean, stdev, minSamples).iterator();
        }

        SpeedSampleCollector collector = new SpeedSampleCollector();
        Object[][] sampleData = new Object[iterators.length][3];
        int runningSequences;
        do {
            runningSequences = 0;
            for (int i=0; i<sampleData.length; i++) {
                String name = (String) data[i][0];
                double value = iterators[i].next();
                long time = (long) (value * iterationsPerSample);

                sampleData[i][0] = name;
                sampleData[i][1] = time;
                sampleData[i][2] = iterationsPerSample;

                if (iterators[i].hasNext()) {
                    runningSequences++;
                }
            }
            PerformanceSample sample = createPerformanceSample(sampleData);
            collector.add(sample);
        } while(runningSequences > 0);

        return collector.createPerformanceStats(false);
    }

    /**
     *
     * @param data
     *        <ol>
     *          <li>name (String)
     *          <li>time (long)
     *          <li>iterations (long)
     *        </ol>
     *
     * @return
     */
    public static PerformanceSample createPerformanceSample(Object[][] data) {
        Map<String, IterationTime> map = new LinkedHashMap<>();
        long totalTimeAccumulator = 0;
        for (Object[] line : data) {
            String name = (String) line[0];
            long time = (long) line[1];
            long iterations = (long) line[2];

            totalTimeAccumulator += time;

            IterationTimeAccumulator it = new IterationTimeAccumulator();
            it.add(time, iterations);

            map.put(name, it);
        }
        return new PerformanceSample(totalTimeAccumulator, map);
    }

    /**
     * Creates the {@link SpeedStats} based on coincidental samples.
     *
     * @param iterations how many iterations
     * @param data array of pairs
     *        <ol>
     *        <li>name (String)
     *        <li>time (long)
     *        <li>memory (long) [optional]
     *        </ol>
     * @return the created {@link SpeedStats}
     */
    public static SpeedStats createCoincidentalStats(
            final long iterations,
            final Object[][] data) {
        SpeedSampleCollector collector = new SpeedSampleCollector();
        final PerformanceSample sample = createSample(iterations, data);
        for (int i=0; i<10; i++) {
            collector.add(sample);
        }
        return collector.createPerformanceStats(false);
    }

    /**
     *
     * @param iterations how many iterations
     * @param data array of pairs
     *        <ol>
     *        <li>name (String)
     *        <li>time (long)
     *        </ol>
     * @return the created {@link PerformanceSample}
     */
    public static PerformanceSample createSample(final long iterations,
            final Object[][] data) {
        IterationTimeCollector collector = new IterationTimeCollector();
        for (Object[] perf: data) {
            final String name = (String) perf[0];
            final long elapsed = (int) perf[1];
            collector.add(name, elapsed * iterations, iterations);
        }
        return collector.createPerformanceSample();
    }

    /**
     * @param data array of triplets:
     *        <ol>
     *        <li>test name (String)
     *        <li>time mean (double)
     *        <li>stdev (double)
     *        </ol>
     * @return
     */
    public static Map<String, TestPerformance> createTestPerformance(
            Object[][] data) {
        Map<String,TestPerformance> map = new LinkedHashMap<>();
        for (Object[] pair : data) {
            String name = (String) pair[0];
            double mean = (double) pair[1];
            double stdev = (double) pair[2];

            Measure m = new NormalDistributionMeasureBuilder(mean, stdev, 100)
                .build();
            map.put(name, new TestPerformance(name, m, 100, 100, 100, 100));
        }
        return map;
    }

    public static void main(final String[] args) {
        System.out.println(createPerformanceStats(100, new Object[][] {
            {"first", 10.0, 5.0, 100},
            {"second", 20.0, 7.0, 100}
        }));
    }
}
