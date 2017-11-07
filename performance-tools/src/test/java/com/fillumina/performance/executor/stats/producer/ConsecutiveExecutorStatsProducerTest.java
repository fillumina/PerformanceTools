package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.mock.StatsMock;
import com.fillumina.performance.mock.StatsProducerMock;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsecutiveExecutorStatsProducerTest {

    @Test
    public void shouldExecuteTestsConsecutively() {
        StatsProducerMock statsProducer = new StatsProducerMock(
                "one", 1.0, "two", 2.0, "three", 3.0);

        ConsecutiveExecutorStatsProducer consecutiveExecutor =
                new ConsecutiveExecutorStatsProducer(true);

        consecutiveExecutor.instrument(statsProducer);

        consecutiveExecutor
                .addTest("one", () -> {})
                .addTest("two", () -> {})
                .addTest("three", () -> {});

        MixedAssertableHolder mixedHolder = consecutiveExecutor.execute();

        StatsMock stats = mixedHolder.getStats(StatsMock.class).getAssertable();

        assertEquals(1.0, stats.getMeasure("one").getMean(), 0);
        assertEquals(2.0, stats.getMeasure("two").getMean(), 0);
        assertEquals(3.0, stats.getMeasure("three").getMean(), 0);

        assertEquals(3, statsProducer.getTestNamesPerExecution().size());
    }

    @Test
    public void shouldSkipExecuteTestsConsecutivelyIfFalseIsPassed() {
        StatsProducerMock statsProducer = new StatsProducerMock(
                "one", 1.0, "two", 2.0, "three", 3.0);

        ConsecutiveExecutorStatsProducer consecutiveExecutor =
                new ConsecutiveExecutorStatsProducer(false);

        consecutiveExecutor.instrument(statsProducer);

        consecutiveExecutor
                .addTest("one", () -> {})
                .addTest("two", () -> {})
                .addTest("three", () -> {});

        MixedAssertableHolder mixedHolder = consecutiveExecutor.execute();

        StatsMock stats = mixedHolder.getStats(StatsMock.class).getAssertable();

        assertEquals(1.0, stats.getMeasure("one").getMean(), 0);
        assertEquals(2.0, stats.getMeasure("two").getMean(), 0);
        assertEquals(3.0, stats.getMeasure("three").getMean(), 0);

        assertEquals(1, statsProducer.getTestNamesPerExecution().size());
        assertEquals(3, statsProducer.getTestNamesPerExecution().get(0).size());
    }
}
