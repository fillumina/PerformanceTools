package com.fillumina.performance.mock;

import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.IterationTimeAccumulator;
import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedSampleCollector;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati
 */
public class MockPerformanceCreator {

    /**
     * Creates the {@link SpeedStats} based on normal distribution.
     *
     * @param iterationsPerSample how many iterations
     * @param tolerance
     * @param data array:
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
            final double tolerance,
            final Object[][] data) {

        @SuppressWarnings("unchecked")
        Iterator<Double>[] iterators = new Iterator[data.length];
        for (int i=0; i<iterators.length; i++) {
            double mean = (double) data[i][1];
            double stdev = (double) data[i][2];
            int minSamples = (int) data[i][3];
            iterators[i] = new NormalDistributionMeasureBuilder(
                    mean, stdev, tolerance, minSamples).iterator();
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
            SpeedSample sample = createPerformanceSample(sampleData);
            collector.add(sample);
        } while(runningSequences > 0);

        return collector.createPerformanceStats(false);
    }

    /**
     *
     * @param data array:
     *        <ol>
     *          <li>name (String)
     *          <li>time (long)
     *          <li>iterations (long)
     *        </ol>
     *
     * @return
     */
    public static SpeedSample createPerformanceSample(Object[][] data) {
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
        return new SpeedSample(totalTimeAccumulator, map);
    }

    /**
     * Creates the {@link SpeedStats} based on coincidental samples.
     *
     * @param iterations how many iterations
     * @param data array:
     *        <ol>
     *        <li>name (String)
     *        <li>iterations (int)
     *        <li>time (long)
     *        </ol>
     * @return the created {@link SpeedStats}
     */
    public static SpeedStats createCoincidentalStats(final Object[][] data) {
        SpeedSampleCollector collector = new SpeedSampleCollector();
        final SpeedSample sample = createSample(data);
        for (int i=0; i<10; i++) {
            collector.add(sample);
        }
        return collector.createPerformanceStats(false);
    }

    /**
     *
     * @param iterations how many iterations
     * @param data array:
     *        <ol>
     *        <li>name (String)
     *        <li>iterations (int)
     *        <li>time (int)
     *        </ol>
     * @return the created {@link SpeedSample}
     */
    public static SpeedSample createSample(final Object[][] data) {
        IterationTimeCollector collector = new IterationTimeCollector();
        for (Object[] perf: data) {
            final String name = (String) perf[0];
            final int iterations = (int) perf[1];
            final long elapsed = (int) perf[2];

            collector.add(name, elapsed * iterations, iterations);
        }
        return collector.createPerformanceSample();
    }

    /**
     * @param data array:
     *        <ol>
     *        <li>test name (String)
     *        <li>time mean (double)
     *        <li>stdev (double)
     *        </ol>
     * @return
     */
    public static Map<String, TestPerformance> createTestPerformance(
            final double tolerance,
            Object[][] data) {
        Map<String,TestPerformance> map = new LinkedHashMap<>();
        for (Object[] pair : data) {
            String name = (String) pair[0];
            double mean = (double) pair[1];
            double stdev = (double) pair[2];

            Measure m = new NormalDistributionMeasureBuilder(
                    mean, stdev, tolerance, 100).build();
            DimensionalMeasure cm = new DimensionalOnlineMeasure(m);
            map.put(name, new TestPerformance(name, cm, 100, 100, 100, 100));
        }
        return map;
    }

    public static void main(final String[] args) {
        System.out.println(createPerformanceStats(100, 0.1, new Object[][] {
            {"first", 10.0, 5.0, 100},
            {"second", 20.0, 7.0, 100}
        }));
    }
}
