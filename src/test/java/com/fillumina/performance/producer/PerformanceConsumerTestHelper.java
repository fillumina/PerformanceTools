package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public abstract class PerformanceConsumerTestHelper {

    public static class ConsumerExecutionChecker
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

    public abstract void executePerformanceProducerWithConsumers(
            final ConsumerExecutionChecker... consumers);

    @Test
    public void shouldThePerformanceTimerCallTheMultipleGivenConsumers() {
        final ConsumerExecutionChecker consumer1 = new ConsumerExecutionChecker();
        final ConsumerExecutionChecker consumer2 = new ConsumerExecutionChecker();

        executePerformanceProducerWithConsumers(consumer1, consumer2);

        assertTrue(consumer1.isCalled());
        assertTrue(consumer2.isCalled());
    }

    @Test
    public void shouldThePerformanceTimerCallTheSingleGivenConsumer() {
        final ConsumerExecutionChecker consumer = new ConsumerExecutionChecker();

        executePerformanceProducerWithConsumers(consumer);

        assertTrue(consumer.isCalled());
    }

}
