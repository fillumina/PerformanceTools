package com.fillumina.performance.stats.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumerChain;
import com.fillumina.performance.sample.AbstractTestable;
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
            final Iterable<PerformanceConsumer<PerformanceStats>> consumers) {

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

                .addPerformanceConsumer(
                        new PerformanceConsumerChain<>(consumers))

                .execute();
    }

}
