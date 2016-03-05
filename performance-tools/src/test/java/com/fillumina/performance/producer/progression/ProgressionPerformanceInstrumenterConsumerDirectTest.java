package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.executor.AbstractTestable;
import com.fillumina.performance.executor.PerformanceTimer;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;

/**
 * The {@link PerformanceTimer} is passed directly to the
 * {@link ProgressionPerformanceInstrumenter}.
 *
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenterConsumerDirectTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final ConsumerExecutionChecker... consumers) {

        final ProgressionPerformanceInstrumenter instrumenter =
            ProgressionPerformanceInstrumenter.builder()
                .setBaseAndMagnitude(1, 1)
                .build();

        final PerformanceTimer pt = PerformanceTimerFactory
            .createSingleThreaded()

            .addTest("example", new AbstractTestable() {

                @Override
                public Object test() {
                    return null;
                }
            });

        instrumenter.instrument(pt);

        instrumenter.addPerformanceConsumer(consumers)
                .execute();
    }

}
