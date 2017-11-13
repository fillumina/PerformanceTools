package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.mock.CountingTestable;
import com.fillumina.performance.mock.NullRunnable;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.time.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.time.sample.iterator.SingleThreadPerformanceExecutor;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.tname.TName;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DefaultPerformanceTimerTest {
    private static final TName ONE = TN.tname("one");

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptZeroIterationsInExecuteInt() {
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());

        pt.addTest(ONE, NullRunnable.INSTANCE);

        pt.iterate(0);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAcceptNoTests() {
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());

        pt.iterate(100);
    }

    @Test
    public void shouldExecuteTheTestsFullyAutomatically() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor()) {
                    @Override
                    public int[] estimateIterations(long milliseconds)
                            throws InvalidTestException {
                        return new int[] {13};
                    }
                };
        pt.addTest(ONE, () -> iterationCounter.incrementAndGet() );
        AverageTimeSample sample = (AverageTimeSample)
                pt.get().get(AverageTimeSample.class);
        assertEquals(13, sample.getValuesMap().get(ONE).getIterations());
    }

    @Test
    public void shouldExecuteATestWithTheGivenNumberOfIterations() {
        AverageTimeSample sample = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public TimeSampleBuilder executeIterations(
                            LinkedMap<TName, Runnable> tests, int[] iterations) {
                        return SpeedSampleMock.builder()
                                .addTest(ONE)
                                    .iterations(iterations[0])
                                    .nansecondsPerOp(100)
                                .endTest()
                                .createSample();
                    }
                })
                .addTest("test", NullRunnable.INSTANCE)
                .iterate(123)
                .buildAverageTimeSample();

        TimeSampleValue value = sample.getValuesMap().get(ONE);

        assertEquals(123, value.getIterations());
        assertEquals(12_300, value.getTimeNs());
    }

    @Test
    public void shouldExecuteTheTestsWithTheGivenNumberOfIterations() {
        AverageTimeSample sample = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public TimeSampleBuilder executeIterations(
                            LinkedMap<TName, Runnable> tests,
                            int[] iterations) {
                        return SpeedSampleMock.builder()
                                .addTest(ONE)
                                    .iterations(iterations[0])
                                    .nansecondsPerOp(100)
                                .endTest()
                                .addTest("two")
                                    .iterations(iterations[1])
                                    .nansecondsPerOp(10)
                                .endTest()
                                .createSample();
                    }
                })
                .addTest("test_1", NullRunnable.INSTANCE)
                .addTest("test_2", NullRunnable.INSTANCE)
                .iterate(new int[] {123, 456})
                .buildAverageTimeSample();

        TimeSampleValue value1 = sample.getValuesMap().get(ONE);
        assertEquals(123, value1.getIterations());
        assertEquals(12_300, value1.getTimeNs());

        TimeSampleValue value2 = sample.getValuesMap().get("two");
        assertEquals(456, value2.getIterations());
        assertEquals(4_560, value2.getTimeNs());
    }

    @Test
    public void shouldExecuteTheTestsWithTheGivenEstimatedIterations() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        int[] iterations = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                        @Override
                        public TimeSampleBuilder executeIterations(
                                LinkedMap<TName, Runnable> tests,
                                int[] iterations) {
                            iterationCounter.set(iterations[0]);
                            return SpeedSampleMock.builder()
                                    .addTest(ONE)
                                        .iterations(iterations[0])
                                        .nansecondsPerOp(250)
                                    .endTest()
                                    .createSample();
                        }
                    })
                .addTest("test", NullRunnable.INSTANCE)
                .estimateIterations(25);

        assertEquals(iterations[0], iterationCounter.get());
    }

    @Test
    public void shouldDispatchTheSampleToConsumers() {
        final AtomicBoolean dispatched = new AtomicBoolean(false);

        final TimeSampleBuilder sample = SpeedSampleMock.builder()
                    .addTest("single")
                        .iterations(123)
                        .nansecondsPerOp(666)
                    .endTest()
                    .createSample();

        new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public TimeSampleBuilder executeIterations(
                            LinkedMap<TName, Runnable> tests,
                            int[] iterations) {
                        return sample;
                    }
                })
                .addTest("test", () -> {})
                .addConsumer((AbstractTimeSample t) -> {
                    dispatched.set(true);
                })
                .executeWithIterations(1);

        assertTrue(dispatched.get());
    }

    @Test
    public void shouldExecuteTestWithWarmup() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());
        pt.addTest(ONE, new Runnable() {
            @Override
            public void run() {
                iterationCounter.incrementAndGet();
            }
        });
        pt.warmup(10);
        assertEquals(10, iterationCounter.get());
    }

    @Test
    public void shouldExecuteTheTestsWithWarmup() {
        CountingTestable ct1 = new CountingTestable();
        CountingTestable ct2 = new CountingTestable();
        new DefaultPerformanceTimer(new SingleThreadPerformanceExecutor())
                .addTest("test_1", ct1)
                .addTest("test_2", ct2)
                .warmup(new int[] {12, 34});

        assertEquals(12, ct1.getCounter());
        assertEquals(34, ct2.getCounter());
    }

    @Test
    public void shouldResetItsTest() {
        DefaultPerformanceTimer pt = new DefaultPerformanceTimer(
            new SingleThreadPerformanceExecutor());
        pt.addTest(ONE, NullRunnable.INSTANCE);
        pt.addTest("two", NullRunnable.INSTANCE);
        AverageTimeSample sample = pt.iterate(1).buildAverageTimeSample();
        assertEquals(2, sample.getValuesMap().size());

        pt.clearTests();
        try {
            pt.iterate(1);
            fail();
        } catch (IllegalStateException e) {
            // ok, no run to iterate
        }
    }

    @Test
    public void shouldBeClose() {
        assertTrue(DefaultPerformanceTimer.close(275149471, 275171084, 0.1));
    }
}
