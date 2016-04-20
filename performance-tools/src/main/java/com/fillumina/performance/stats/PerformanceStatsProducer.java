package com.fillumina.performance.stats;

import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.sample.PerformanceSampleConsumer;

/**
 * A {@link PerformanceStatsProducer} contains none or some
 * {@link PerformanceSampleConsumer}s that it notifies about the performances it
 * collects.
 *
 * @author Francesco Illuminati
 */
public interface PerformanceStatsProducer {

    PerformanceStatsProducer addPerformanceConsumer(
            final PerformanceStatsConsumer... consumers);

    PerformanceStatsProducer removePerformanceConsumer(
            final PerformanceStatsConsumer... consumers);
}
