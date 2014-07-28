package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;
import com.fillumina.performance.producer.timer.PerformanceTimer;

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
