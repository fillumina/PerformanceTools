package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;

/**
 * It uses a fluent interface to make the instrumenter
 * instruments the {@link com.fillumina.performance.producer.timer.PerformanceTimer}.
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionPerformanceInstrumenterConsumerInverseTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final PerformanceConsumerTestHelper.ConsumerExecutionChecker... consumers) {

        PerformanceTimerFactory
            .createSingleThreaded()

            .addTest("example", new Runnable() {

                @Override
                public void run() {
                    // do nothing
                }
            })

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                .setBaseAndMagnitude(1, 1)
                .build())
            .addPerformanceConsumer(consumers)
            .execute();
    }

}
