package com.fillumina.performance.speed.stats;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumerExecutionChecker;
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
            final Iterable<PerformanceConsumer<SpeedStats>> consumers);

    @Test
    public void shouldThePerformanceTimerCallMultipleConsumers() {
        final PerformanceConsumerExecutionChecker<SpeedStats> consumer1 =
                new PerformanceConsumerExecutionChecker<>();
        final PerformanceConsumerExecutionChecker<SpeedStats> consumer2 =
                new PerformanceConsumerExecutionChecker<>();

        executePerformanceProducerWithConsumers(
                Arrays.asList((PerformanceConsumer<SpeedStats>)
                        consumer1, consumer2));

        assertTrue(consumer1.isNotified());
        assertTrue(consumer2.isNotified());
    }

    @Test
    public void shouldThePerformanceTimerCallConsumer() {
        final PerformanceConsumerExecutionChecker<SpeedStats> consumer =
                new PerformanceConsumerExecutionChecker<>();

        executePerformanceProducerWithConsumers(
                Collections.singleton((PerformanceConsumer<SpeedStats>)consumer));

        assertTrue(consumer.isNotified());
    }

}
