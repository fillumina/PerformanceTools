package com.fillumina.performance.stats;

import com.fillumina.performance.sample.IterationTimeCollector;
import com.fillumina.performance.sample.PerformanceSample;

/**
 *
 * @author Francesco Illuminati
 */
public class FakePerformanceCreator {

    public static PerformanceStats createStats(final long iterations,
            final Object[][] data) {
        PerformanceDataCollector collector = new PerformanceDataCollector();
        final PerformanceSample sample = createSample(iterations, data);
        for (int i=0; i<10; i++) {
            collector.add(sample);
        }
        return collector.createPerformanceStats(null, false);
    }

    public static PerformanceSample createSample(final long iterations,
            final Object[][] data) {
        IterationTimeCollector collector = new IterationTimeCollector();
        for (Object[] perf: data) {
            final String name = (String) perf[0];
            final long elapsed = (int) perf[1];
            collector.add(name, elapsed, iterations);
        }
        return collector.createPerformanceSample();
    }
}
