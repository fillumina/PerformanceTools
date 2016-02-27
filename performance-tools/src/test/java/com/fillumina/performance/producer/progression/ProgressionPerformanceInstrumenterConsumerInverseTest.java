package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;
import com.fillumina.performance.producer.timer.AbstractTestable;

/**
 * It uses a
 * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
 * fluent interface</a></i> to make the instrumenter
 * instruments the {@link com.fillumina.performance.producer.timer.PerformanceTimer}.
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenterConsumerInverseTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final PerformanceConsumerTestHelper.ConsumerExecutionChecker... consumers) {

        PerformanceTimerFactory
            .createSingleThreaded()

            .addTest("example", new AbstractTestable() {

                @Override
                public Object test() {
                    return null;
                }
            })

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                .setBaseAndMagnitude(1, 1)
                .build())
            .addPerformanceConsumer(consumers)
            .execute();
    }

}
