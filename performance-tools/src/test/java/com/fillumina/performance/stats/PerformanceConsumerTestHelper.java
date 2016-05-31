package com.fillumina.performance.stats;

import com.fillumina.performance.infrastructure.ConsumerExecutionChecker;
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
            final Iterable<ConsumerExecutionChecker<PerformanceStats>> consumers);

    @Test
    public void shouldThePerformanceTimerCallTheMultipleGivenConsumers() {
        final ConsumerExecutionChecker<PerformanceStats> consumer1 =
                new ConsumerExecutionChecker<>();
        final ConsumerExecutionChecker<PerformanceStats> consumer2 =
                new ConsumerExecutionChecker<>();

        executePerformanceProducerWithConsumers(
                Arrays.asList(consumer1, consumer2));

        assertTrue(consumer1.isCalled());
        assertTrue(consumer2.isCalled());
    }

    @Test
    public void shouldThePerformanceTimerCallTheSingleGivenConsumer() {
        final ConsumerExecutionChecker<PerformanceStats> consumer =
                new ConsumerExecutionChecker<>();

        executePerformanceProducerWithConsumers(Collections.singleton(consumer));

        assertTrue(consumer.isCalled());
    }

}
