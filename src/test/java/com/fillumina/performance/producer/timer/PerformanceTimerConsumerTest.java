package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class PerformanceTimerConsumerTest {

    private static class ConsumerExecutionChecker
            implements PerformanceConsumer {

        private boolean called = false;

        @Override
        public void consume(final String message,
                final LoopPerformances loopPerformances) {
            called = true;
        }

        public boolean isCalled() {
            return called;
        }
    }

    @Test
    public void shouldThePerformanceTimerCallTheGivenConsumer() {
        final ConsumerExecutionChecker checker =
                new ConsumerExecutionChecker();

        PerformanceTimerBuilder
                .createSingleThread()

                .addTest("example", new Runnable() {

                    @Override
                    public void run() {
                        // do nothing
                    }
                })

                .addPerformanceConsumer(checker)

                .iterate(1);

        assertTrue(checker.isCalled());
    }
}
