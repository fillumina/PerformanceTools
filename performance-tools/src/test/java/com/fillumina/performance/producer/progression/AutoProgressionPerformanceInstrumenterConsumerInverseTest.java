package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;
import com.fillumina.performance.producer.timer.AbstractTestable;

/**
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionPerformanceInstrumenterConsumerInverseTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final ConsumerExecutionChecker... consumers) {

        PerformanceTimerFactory
                .createSingleThreaded()

                .addTest("example", new AbstractTestable() {

                    @Override
                    public Object test() {
                        return null;
                    }
                })

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setMaxStandardDeviation(1)
                    .build())
                .addPerformanceConsumer(consumers)
                .execute();
    }

}
