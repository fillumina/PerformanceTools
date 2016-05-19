package com.fillumina.performance.stats.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.stats.ConsumerExecutionChecker;
import com.fillumina.performance.stats.PerformanceConsumerTestHelper;

/**
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionPerformanceInstrumenterConsumerTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final Iterable<ConsumerExecutionChecker> consumers) {

        PerformanceTimerFactory
                .createSingleThreaded()

//                .addPerformanceConsumer(StringCsvSampleViewer.INSTANCE)

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setSamples(10)
                        .setBaseIterations(10)
                        .setMaxPercentageMargin(30)
                        .setTimeoutSeconds(5)
                        .build())

                .addTest("example", new AbstractTestable() {
                    private int counter;

                    @Override
                    public Object test() {
                        return counter++;
                    }
                })

//                .addPerformanceConsumer(StringTableStatsViewer.INSTANCE)

                .addPerformanceConsumer(consumers)

                .execute();
    }

}
