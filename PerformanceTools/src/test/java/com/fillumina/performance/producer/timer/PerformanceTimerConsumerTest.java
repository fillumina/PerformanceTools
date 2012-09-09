package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.PerformanceConsumerTestHelper;
import com.fillumina.performance.PerformanceTimerBuilder;

/**
 *
 * @author fra
 */
public class PerformanceTimerConsumerTest
    extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final ConsumerExecutionChecker... consumers) {

        PerformanceTimerBuilder
                .createSingleThread()

                .addTest("example", new Runnable() {

                    @Override
                    public void run() {
                        // do nothing
                    }
                })

                .addPerformanceConsumer(consumers)

                .iterate(1);
    }
}
