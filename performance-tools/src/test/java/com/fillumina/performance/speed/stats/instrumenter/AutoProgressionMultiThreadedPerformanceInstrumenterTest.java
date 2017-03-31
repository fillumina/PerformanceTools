package com.fillumina.performance.speed.stats.instrumenter;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumerChain;
import com.fillumina.performance.infrastructure.Testable;
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

                .addTest("example", new Testable() {

                    @Override
                    public void test() {
                    }
                })

                .addPerformanceConsumer(
                        new PerformanceConsumerChain<>(consumers))

                .execute();
    }

}
