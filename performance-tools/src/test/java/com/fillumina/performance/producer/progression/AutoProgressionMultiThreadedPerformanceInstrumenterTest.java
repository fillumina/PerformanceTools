package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;

/**
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionMultiThreadedPerformanceInstrumenterTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final ConsumerExecutionChecker... consumers) {

        PerformanceTimerFactory.getMultiThreadedBuilder()
                .setConcurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build()

                .addTest("example", new Runnable() {

                    @Override
                    public void run() {
                        // do nothing
                    }
                })

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setMaxStandardDeviation(1)
                    .build())
                .addPerformanceConsumer(consumers)
                .execute();
    }

}
