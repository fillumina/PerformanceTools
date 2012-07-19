package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;

/**
 *
 * @author fra
 */
public class AutoProgressionPerformanceInstrumenterConsumerTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final ConsumerExecutionChecker... consumers) {

        PerformanceTimerBuilder
                .createSingleThread()

                .addTest("example", new Runnable() {

                    @Override
                    public void run() {
                        // do nothing
                    }
                })

                .instrumentedBy(new AutoProgressionPerformanceInstrumenter.Builder())
                .setMaxStandardDeviation(1)
                .build()
                .addPerformanceConsumer(consumers)
                .executeSequence();
    }

}
