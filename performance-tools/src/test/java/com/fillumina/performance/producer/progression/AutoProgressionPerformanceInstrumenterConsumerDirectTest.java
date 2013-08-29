package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;
import com.fillumina.performance.producer.timer.PerformanceTimer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceInstrumenterConsumerDirectTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final ConsumerExecutionChecker... consumers) {

        final AutoProgressionPerformanceInstrumenter instrumenter =
            AutoProgressionPerformanceInstrumenter.builder()
                .setMaxStandardDeviation(1)
                .build();

        final PerformanceTimer pt = PerformanceTimerBuilder
            .createSingleThreaded()

            .addTest("example", new Runnable() {

                @Override
                public void run() {
                    // do nothing
                }
            });

        instrumenter.instrument(pt);

        instrumenter.addPerformanceConsumer(consumers)
                .execute();
    }

}
