package com.fillumina.performance.stats;

import com.fillumina.performance.sample.PerformanceSample;

/**
 *
 * @author Francesco Illuminati
 */
public class FakePerformanceCreator {

    public static PerformanceStats createStats(final long iterations,
            final Object[][] data) {
        PerformanceDataCollector collector = new PerformanceDataCollector();
        for (int i=0; i<10; i++) {
            collector.add(createSample(iterations, data));
        }
        return collector.createPerformanceStats();
    }

    public static PerformanceSample createSample(final long iterations,
            final Object[][] data) {
        PerformanceSample sample = new PerformanceSample();
        for (Object[] perf: data) {
            final String name = (String) perf[0];
            final long elapsed = (int) perf[1];
            sample.add(name, elapsed, iterations);
        }
        return sample;
    }
}
