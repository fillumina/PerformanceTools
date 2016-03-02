package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;
import com.fillumina.performance.executor.AbstractTestable;

/**
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionPerformanceInstrumenterConsumerDirectTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final ConsumerExecutionChecker... consumers) {

        AutoProgressionPerformanceInstrumenter.builder()
            .setMaxStandardDeviation(1)
            .build()
            .instrument(PerformanceTimerFactory
                .createSingleThreaded()
                .addTest("example", new AbstractTestable() {

                    @Override
                    public Object test() {
                        return null;
                    }
                }))
            .addPerformanceConsumer(consumers)
            .execute();
    }

}
