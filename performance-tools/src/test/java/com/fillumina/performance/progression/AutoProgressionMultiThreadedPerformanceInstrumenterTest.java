package com.fillumina.performance.progression;

import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.stats.ConsumerExecutionChecker;
import com.fillumina.performance.stats.PerformanceConsumerTestHelper;

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

                .addTest("example", new AbstractTestable() {

                    @Override
                    public Object test() {
                        return null;
                    }
                })

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setMinConfidence(.90)
                    .build())
                .addPerformanceConsumer(consumers)
                .execute();
    }

}
