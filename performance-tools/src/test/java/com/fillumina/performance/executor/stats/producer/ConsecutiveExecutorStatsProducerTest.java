package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mock.MockStatsType;
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
        TestNamesStatsProducerMock statsProducer = new TestNamesStatsProducerMock(
                "one", 1.0, "two", 2.0, "three", 3.0);

        ConsecutiveExecutorStatsProducer consecutiveExecutor =
                new ConsecutiveExecutorStatsProducer(true);

        consecutiveExecutor.instrument(statsProducer);

        consecutiveExecutor
                .addTest("one", () -> {})
                .addTest("two", () -> {})
                .addTest("three", () -> {});

        MixedStatsHolder mixedHolder = consecutiveExecutor.execute();

        Stats stats = mixedHolder.getHolder(MockStatsType.INSTANCE).getStats();

        assertEquals(1.0, stats.getMeasure("one").getMean(), 0);
        assertEquals(2.0, stats.getMeasure("two").getMean(), 0);
        assertEquals(3.0, stats.getMeasure("three").getMean(), 0);

        assertEquals(3, statsProducer.getEvaluatedTree().size());
    }

    @Test
    public void shouldSkipExecuteTestsConsecutivelyIfFalseIsPassed() {
        TestNamesStatsProducerMock statsProducer = new TestNamesStatsProducerMock(
                "one", 1.0, "two", 2.0, "three", 3.0);

        ConsecutiveExecutorStatsProducer consecutiveExecutor =
                new ConsecutiveExecutorStatsProducer(false);

        consecutiveExecutor.instrument(statsProducer);

        consecutiveExecutor
                .addTest("one", () -> {})
                .addTest("two", () -> {})
                .addTest("three", () -> {});

        MixedStatsHolder mixedHolder = consecutiveExecutor.execute();

        Stats stats = mixedHolder.getHolder(MockStatsType.INSTANCE).getStats();

        assertEquals(1.0, stats.getMeasure("one").getMean(), 0);
        assertEquals(2.0, stats.getMeasure("two").getMean(), 0);
        assertEquals(3.0, stats.getMeasure("three").getMean(), 0);

        assertEquals(1, statsProducer.getEvaluatedTree().size());
        assertEquals(3, statsProducer.getEvaluatedTree().get(0).size());
    }

    private static class TestNamesStatsProducerMock
            extends StatsProducerMock<CharSequence> {

        public TestNamesStatsProducerMock(Object... objects) {
            super(objects);
        }

        @Override
        public CharSequence evaluate(CharSequence testName, Runnable test) {
            return testName;
        }
    }
}
