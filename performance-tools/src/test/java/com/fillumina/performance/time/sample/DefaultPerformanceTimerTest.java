package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.mock.CountingTestable;
import com.fillumina.performance.mock.NullRunnable;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.time.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.time.sample.iterator.SingleThreadPerformanceExecutor;
import com.fillumina.performance.util.collection.ArrayMap;
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
    public void shouldExecuteATestWithTheGivenNumberOfIterations() {
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
        pt.execute();
        assertEquals(13, iterationCounter.get(), 0);
    }

    @Test
    public void shouldExecuteTheTestsWithTheGivenNumberOfIterations() {
        Sample sample = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public TimeSampleBuilder executeIterations(
                            ArrayMap<TName, Runnable> tests,
                            int[] iterations) {
                        return SpeedSampleMock.builder()
                                .addTest(ONE)
                                    .iterations(iterations[0])
                                    .nansecondsPerOp(123)
                                .endTest()
                                .addTest("two")
                                    .iterations(iterations[1])
                                    .nansecondsPerOp(456)
                                .endTest()
                                .createSample();
                    }
                })
                .addTest("test_1", NullRunnable.INSTANCE)
                .addTest("test_2", NullRunnable.INSTANCE)
                .iterate(1)
                .buildAverageTimeSample();

        SampleValue value1 = sample.getValuesMap().get(ONE);
        assertEquals(123, value1.getQuantity().toBase(), 0);

        SampleValue value2 = sample.getValuesMap().get("two");
        assertEquals(456, value2.getQuantity().toBase(), 0);
    }

    @Test
    public void shouldExecuteTheTestsWithTheGivenEstimatedIterations() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        int[] iterations = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                        @Override
                        public TimeSampleBuilder executeIterations(
                                ArrayMap<TName, Runnable> tests,
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
                            ArrayMap<TName, Runnable> tests,
                            int[] iterations) {
                        return sample;
                    }
                })
                .addTest("test", () -> {})
                .addConsumer((Sample t) -> {
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
        Sample sample = pt.iterate(1).buildAverageTimeSample();
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
    public void shouldShuffleArrayOfSize() {
        int[] a = DefaultPerformanceTimer.getShuffledArrayOfSize(20);
        int[] b = DefaultPerformanceTimer.getShuffledArrayOfSize(20);

        boolean equals = true;
        for (int i=0; i<a.length; i++) {
            assertNotEquals(i, a[i]);
            assertNotEquals(i, b[i]);
            equals = equals && (a[i] == b[i]);
        }
        assertFalse(equals);
    }

    @Test
    public void shouldBeClose() {
        assertTrue(DefaultPerformanceTimer.close(275149471, 275171084, 0.1));
    }
}
