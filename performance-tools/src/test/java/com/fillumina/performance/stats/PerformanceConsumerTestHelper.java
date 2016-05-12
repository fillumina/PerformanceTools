package com.fillumina.performance.stats;

import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class PerformanceConsumerTestHelper {


    public abstract void executePerformanceProducerWithConsumers(
            final Iterable<ConsumerExecutionChecker> consumers);

    @Test
    public void shouldThePerformanceTimerCallTheMultipleGivenConsumers() {
        final ConsumerExecutionChecker consumer1 = new ConsumerExecutionChecker();
        final ConsumerExecutionChecker consumer2 = new ConsumerExecutionChecker();

        executePerformanceProducerWithConsumers(
                Arrays.asList(consumer1, consumer2));

        assertTrue(consumer1.isCalled());
        assertTrue(consumer2.isCalled());
    }

    @Test
    public void shouldThePerformanceTimerCallTheSingleGivenConsumer() {
        final ConsumerExecutionChecker consumer = new ConsumerExecutionChecker();

        executePerformanceProducerWithConsumers(Collections.singleton(consumer));

        assertTrue(consumer.isCalled());
    }

}
