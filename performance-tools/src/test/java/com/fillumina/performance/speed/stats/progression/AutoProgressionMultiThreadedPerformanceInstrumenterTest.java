package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumerChain;
import com.fillumina.performance.infrastructure.AbstractTestable;
import com.fillumina.performance.speed.stats.PerformanceConsumerTestHelper;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionMultiThreadedPerformanceInstrumenterTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final Iterable<PerformanceConsumer<SpeedStats>> consumers) {

        PerformanceTimerFactory.getMultiThreadedBuilder()
                .setConcurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build()

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setMaxPercentageMargin(100)
                    .build())

                .addTest("example", new AbstractTestable() {

                    @Override
                    public void test() {
                    }
                })

                .addPerformanceConsumer(
                        new PerformanceConsumerChain<>(consumers))

                .execute();
    }

}
