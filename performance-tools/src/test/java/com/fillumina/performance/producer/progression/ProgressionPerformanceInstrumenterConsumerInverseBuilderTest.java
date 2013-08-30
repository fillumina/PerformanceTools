package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;

/**
 * It uses a fluent interface where the instrumenter builder implements
 * {@link com.fillumina.performance.producer.PerformanceExecutorInstrumenter}
 * itself and so can be passed to the
 * {@link com.fillumina.performance.producer.InstrumentablePerformanceExecutor#instrumentedBy(com.fillumina.performance.producer.PerformanceExecutorInstrumenter) }.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionPerformanceInstrumenterConsumerInverseBuilderTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final PerformanceConsumerTestHelper.ConsumerExecutionChecker... consumers) {

        PerformanceTimerBuilder
            .createSingleThreaded()

            .addTest("example", new Runnable() {

                @Override
                public void run() {
                    // do nothing
                }
            })

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                .setBaseAndMagnitude(1, 1)
                .build())
            .addPerformanceConsumer(consumers)
            .execute();
    }

}
