package com.fillumina.performance.stats.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.PerformanceConsumerExecutionChecker;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.stats.PerformanceConsumerTestHelper;
import com.fillumina.performance.stats.PerformanceStats;

/**
 * It uses a
 * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
 * fluent interface</a></i> to make the instrumenter
 * instruments the {@link com.fillumina.performance.sample.DefaultPerformanceTimer}.
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenterConsumerTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final Iterable<PerformanceConsumerExecutionChecker<PerformanceStats>> consumers) {

        PerformanceTimerFactory
            .createSingleThreaded()

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                .setBaseAndMagnitude(1, 1)
                .build())

            .addTest("example", new AbstractTestable() {

                @Override
                public Object test() {
                    return null;
                }
            })

            .addPerformanceConsumer(consumers)

            .execute();
    }

}
