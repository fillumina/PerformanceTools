package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.PerformanceConsumerTestHelper;
import com.fillumina.performance.PerformanceTimerFactory;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimerConsumerTest
    extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final ConsumerExecutionChecker... consumers) {

        PerformanceTimerFactory
                .createSingleThreaded()

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
