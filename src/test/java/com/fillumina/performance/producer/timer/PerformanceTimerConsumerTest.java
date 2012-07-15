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
    public void createProducer(final ConsumerExecutionChecker consumer) {
        PerformanceTimerBuilder
                .createSingleThread()

                .addTest("example", new Runnable() {

                    @Override
                    public void run() {
                        // do nothing
                    }
                })

                .addPerformanceConsumer(consumer)

                .iterate(1);
    }

    @Override
    public void createProducer(final ConsumerExecutionChecker consumer1,
            final ConsumerExecutionChecker consumer2) {
        PerformanceTimerBuilder
                .createSingleThread()

                .addTest("example", new Runnable() {

                    @Override
                    public void run() {
                        // do nothing
                    }
                })

                .addPerformanceConsumer(consumer1)
                .addPerformanceConsumer(consumer2)

                .iterate(1);
    }
}
