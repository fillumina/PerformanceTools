package com.fillumina.performance.speed.sample;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.mock.CountingTestable;
import com.fillumina.performance.mock.MockPerformanceCreator;
import com.fillumina.performance.mock.NullTestable;
import com.fillumina.performance.speed.sample.executor.PerformanceExecutor;
import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;
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

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptZeroIterationsInExecuteInt() {
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());

        pt.addTest("one", NullTestable.INSTANCE);

        pt.execute(0);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAcceptNoTests() {
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());

        pt.execute(100);
    }

    @Test
    public void shouldExecuteTheTestsFullyAutomatically() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());
        pt.addTest("one", new Testable() {
            @Override
            public void test() {
                iterationCounter.incrementAndGet();
            }
        });
        SpeedSample sample = pt.execute().getStats();
        assertTrue(sample.getTimeMap().get("one").getIterations() > 0);
    }

    @Test
    public void shouldExecuteATestWithTheGivenNumberOfIterations() {
        SpeedSample sample = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            LinkedHashMap<String, Testable> tests,
                            int[] iterations) {
                        return MockPerformanceCreator.createSample(
                                new Object[][]{{"one", iterations[0], 100}});
                    }
                })
                .addTest("test", NullTestable.INSTANCE)
                .execute(123);

        IterationTime it = sample.getTimeMap().get("one");

        assertEquals(123, it.getIterations());
        assertEquals(12_300, it.getTimeNs());
    }

    @Test
    public void shouldExecuteTheTestsWithTheGivenNumberOfIterations() {
        SpeedSample sample = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            LinkedHashMap<String, Testable> tests,
                            int[] iterations) {
                        return MockPerformanceCreator.createSample(
                                new Object[][]{
                                    {"one", iterations[0], 100},
                                    {"two", iterations[1], 10}
                                });
                    }
                })
                .addTest("test_1", NullTestable.INSTANCE)
                .addTest("test_2", NullTestable.INSTANCE)
                .execute(new int[] {123, 456});

        IterationTime it1 = sample.getTimeMap().get("one");
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
                                LinkedHashMap<String, Testable> tests,
                                int[] iterations) {
                            iterationCounter.set(iterations[0]);
                            return MockPerformanceCreator.createSample(
                                    new Object[][]{{"one", iterations[0], 250}});
                        }
                    })
                .addTest("test", NullTestable.INSTANCE)
                .iterationTimeEstimator(25);

        assertEquals(iterations[0], iterationCounter.get());
    }

    @Test
    public void shouldDispatchTheSampleToConsumers() {
        final AtomicBoolean dispatched = new AtomicBoolean(false);

        final SpeedSample sample = MockPerformanceCreator.createSample(
                        new Object[][]{{"single", 123, 666}});

        new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            LinkedHashMap<String, Testable> tests,
                            int[] iterations) {
                        return sample;
                    }
                })
                .addTest("test", NullTestable.INSTANCE)
                .addPerformanceConsumer(new PerformanceConsumer<SpeedSample>() {
                    @Override
                    public void consume(PHolder<SpeedSample> holder) {
                        SpeedSample performances = holder.getStats();
                        dispatched.set(true);
                        assertTrue(sample == performances);
                    }
                })
                .execute(1);

        assertTrue(dispatched.get());
    }

    @Test
    public void shouldExecuteTestWithWarmup() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());
        pt.addTest("one", new Testable() {
            @Override
            public void test() {
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
    public void shouldSetupAndTearDownAlternately() {
        final AtomicInteger setUpCounter = new AtomicInteger(0);
        final AtomicInteger tearDownCounter = new AtomicInteger(0);
        final AtomicBoolean initialized = new AtomicBoolean(false);

        DefaultPerformanceTimer pt = new DefaultPerformanceTimer(
            new SingleThreadPerformanceExecutor());
        pt.addTest("one", new Testable() {
            int i = 0;

            @Override
            public void test() {
                Sink.drain(i++);
            }

            @Override
            public void setUp() {
                if (initialized.get()) {
                    throw new AssertionError("already initialized");
                }
                initialized.set(true);
                setUpCounter.incrementAndGet();
            }

            @Override
            public void tearDown() {
                if (!initialized.get()) {
                    throw new AssertionError("not initialized");
                }
                initialized.set(false);
                tearDownCounter.incrementAndGet();
            }

        });
        assertEquals(0, setUpCounter.get());
        assertEquals(0, tearDownCounter.get());

        pt.iterationTimeEstimator(10); // setUp() & tearDown()

        assertEquals(1, setUpCounter.get());
        assertEquals(1, tearDownCounter.get());

        pt.warmup(10); // setUp() & tearDown()

        assertEquals(2, setUpCounter.get());
        assertEquals(2, tearDownCounter.get());

        pt.execute(10); // setUp() & tearDown()

        assertFalse(initialized.get());
        assertEquals(3, setUpCounter.get());
        assertEquals(3, tearDownCounter.get());
    }

    @Test
    public void shouldResetItsTest() {
        DefaultPerformanceTimer pt = new DefaultPerformanceTimer(
            new SingleThreadPerformanceExecutor());
        pt.addTest("one", NullTestable.INSTANCE);
        pt.addTest("two", NullTestable.INSTANCE);
        SpeedSample sample = pt.execute(1);
        assertEquals(2, sample.getTimeMap().size());

        pt.clearTests();
        try {
            pt.execute(1);
            fail();
        } catch (IllegalStateException e) {
            // ok, no test to execute
        }
    }

}
