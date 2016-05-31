package com.fillumina.performance.stats.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.ConsumerExecutionChecker;
import com.fillumina.performance.infrastructure.AbstractTestable;
import com.fillumina.performance.stats.PerformanceConsumerTestHelper;
import com.fillumina.performance.stats.PerformanceStats;

/**
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionMultiThreadedPerformanceInstrumenterTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final Iterable<ConsumerExecutionChecker<PerformanceStats>> consumers) {

        PerformanceTimerFactory.getMultiThreadedBuilder()
                .setConcurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build()

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setMinConfidence(.90)
                    .setMaxPercentageMargin(10)
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
