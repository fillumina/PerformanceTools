package com.fillumina.performance.speed.sample;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.CountingTestable;
import com.fillumina.performance.mock.NullTestable;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.speed.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.speed.sample.iterator.SingleThreadPerformanceExecutor;
import com.fillumina.performance.util.TName;
import java.util.LinkedHashMap;
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

        pt.addTest(ONE, NullTestable.INSTANCE);

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
                new SingleThreadPerformanceExecutor());
        pt.addTest(ONE, new Runnable() {
            @Override
            public void run() {
                iterationCounter.incrementAndGet();
            }
        });
        SpeedSample sample = pt.execute().getAssertable();
        assertTrue(sample.getTimeMap().get(ONE).getIterations() > 0);
    }

    @Test
    public void shouldExecuteATestWithTheGivenNumberOfIterations() {
        SpeedSample sample = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            LinkedHashMap<TName, Runnable> tests,
                            int[] iterations) {
                        return SpeedSampleMock.builder()
                                .addTest(ONE)
                                    .iterations(iterations[0])
                                    .nansecondsPerOp(100)
                                .endTest()
                                .createSample();
                    }
                })
                .addTest("test", NullTestable.INSTANCE)
                .iterate(123);

        IterationTime it = sample.getTimeMap().get(ONE);

        assertEquals(123, it.getIterations());
        assertEquals(12_300, it.getTimeNs());
    }

    @Test
    public void shouldExecuteTheTestsWithTheGivenNumberOfIterations() {
        SpeedSample sample = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            LinkedHashMap<TName, Runnable> tests,
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
                .addTest("test_1", NullTestable.INSTANCE)
                .addTest("test_2", NullTestable.INSTANCE)
                .iterate(new int[] {123, 456});

        IterationTime it1 = sample.getTimeMap().get(ONE);
        assertEquals(123, it1.getIterations());
        assertEquals(12_300, it1.getTimeNs());

        IterationTime it2 = sample.getTimeMap().get("two");
        assertEquals(456, it2.getIterations());
        assertEquals(4_560, it2.getTimeNs());
    }

    @Test
    public void shouldExecuteTheTestsWithTheGivenEstimatedIterations() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        int[] iterations = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                        @Override
                        public SpeedSample executeTests(
                                LinkedHashMap<TName, Runnable> tests,
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
                .addTest("test", NullTestable.INSTANCE)
                .iterationTimeEstimatorMs(25);

        assertEquals(iterations[0], iterationCounter.get());
    }

    @Test
    public void shouldDispatchTheSampleToConsumers() {
        final AtomicBoolean dispatched = new AtomicBoolean(false);

        final SpeedSample sample = SpeedSampleMock.builder()
                    .addTest("single")
                        .iterations(123)
                        .nansecondsPerOp(666)
                    .endTest()
                    .createSample();

        new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            LinkedHashMap<TName, Runnable> tests,
                            int[] iterations) {
                        return sample;
                    }
                })
                .addTest("test", NullTestable.INSTANCE)
                .addPerformanceConsumer(new PerformanceConsumer<SpeedSample>() {
                    @Override
                    public void consume(SpeedSample performances) {
                        dispatched.set(true);
                        assertTrue(sample == performances);
                    }
                })
                .iterate(1);

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
        pt.addTest(ONE, NullTestable.INSTANCE);
        pt.addTest("two", NullTestable.INSTANCE);
        SpeedSample sample = pt.iterate(1);
        assertEquals(2, sample.getTimeMap().size());

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
