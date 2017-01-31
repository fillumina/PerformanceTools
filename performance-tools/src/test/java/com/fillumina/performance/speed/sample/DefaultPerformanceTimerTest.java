package com.fillumina.performance.speed.sample;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.NullTest;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.executor.PerformanceExecutor;
import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedStatsTableStringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import org.junit.Ignore;
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
        pt.addTest("one", NullTest.INSTANCE);
        pt.execute(0);
    }

    @Test
    public void shouldExecuteTheTestsFullyAutomatically() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());
        pt.addTest("one", new AbstractTestable() {
            @Override
            public Object test() {
                iterationCounter.addAndGet(1);
                return null;
            }
        });
        SpeedSample sample = pt.execute().getTree();
        assertTrue(sample.getTimeMap().get("one").getIterations() > 0);
    }

    @Test
    public void shouldExecuteATestWithTheGivenNumberOfIterations() {
        SpeedSample sample = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            Map<String, Testable> tests, int[] iterations) {
                        return FakePerformanceCreator.createSample(iterations[0],
                                new Object[][]{{"one", 100}});
                    }
                })
                .addTest("test", NullTest.INSTANCE)
                .execute(123);

        assertEquals(123, sample.getTimeMap().get("one").getIterations());
    }

    @Test
    public void shouldEstimateTheNumberOfIterationInGivenTime() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        int[] iterations = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                        @Override
                        public SpeedSample executeTests(
                                Map<String, Testable> tests, int[] iterations) {
                            iterationCounter.set(iterations[0]);
                            return FakePerformanceCreator.createSample(iterations[0],
                                new Object[][]{{"one", 250_000_000}});
                        }
                    })
                .addTest("test", NullTest.INSTANCE)
                .iterationTimeEstimator(25);
        assertEquals(iterations[0], iterationCounter.get());
    }

    @Test
    public void shouldDispatchTheSampleToConsumers() {
        final AtomicBoolean dispatched = new AtomicBoolean(false);
        final SpeedSample sample = FakePerformanceCreator.createSample(123,
                        new Object[][]{{"single", 666}});
        new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            Map<String, Testable> tests, int[] iterations) {
                        return sample;
                    }
                })
                .addTest("test", NullTest.INSTANCE)
                .addPerformanceConsumer(new PerformanceConsumer<SpeedSample>() {
                    @Override
                    public void consume(ComposedName message,
                            SpeedSample performances) {
                        dispatched.set(true);
                        assertTrue(sample == performances);
                    }
                })
                .execute(1);

        assertTrue(dispatched.get());
    }

    @Test
    public void shouldExecuteTestsWithWarmup() {
        final AtomicInteger iterationCounter = new AtomicInteger(0);
        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());
        pt.addTest("one", new AbstractTestable() {
            @Override
            public Object test() {
                iterationCounter.addAndGet(1);
                return null;
            }
        });
        pt.warmup(10);
        assertEquals(10, iterationCounter.get());
    }

    @Test
    public void shouldIniTestOnlyOnce() {
        final AtomicInteger initialized = new AtomicInteger(0);
        DefaultPerformanceTimer pt = new DefaultPerformanceTimer(
            new SingleThreadPerformanceExecutor());
        pt.addTest("one", new AbstractTestable() {
            int i = 0;

            @Override
            public Object test() {
                return i++;
            }

            @Override
            public void setUp() {
                initialized.addAndGet(1);
            }
        });
        pt.iterationTimeEstimator(10);
        pt.warmup(10);
        pt.execute(10);
        assertEquals(1, initialized.get());
    }

    @Test
    public void shouldResetItsTest() {
        DefaultPerformanceTimer pt = new DefaultPerformanceTimer(
            new SingleThreadPerformanceExecutor());
        pt.addTest("one", NullTest.INSTANCE);
        pt.addTest("two", NullTest.INSTANCE);
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

    // JVM isn't that aggressive on optimizations when testing
    @Ignore @Test(expected = RuntimeException.class)
    public void shouldDetectCodeEviction() {
        System.out.println("runtime");
        PerformanceTimerFactory.createSingleThreaded()
                .addTest("test", NullTest.INSTANCE)
                .iterationTimeEstimator(25);
    }

    public static void main(final String[] args) {

        PerformanceTimerFactory.createSingleThreaded()
                .addPerformanceConsumer(SampleLineStringGenerator.VIEWER)
                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                            .setTimeout(60, TimeUnit.SECONDS)
                            .setMaxPercentageMargin(3)
                            .build())
                .addTest("null", NullTest.INSTANCE)
                .addPerformanceConsumer(SpeedStatsTableStringGenerator.VIEWER)

                .execute()
                .print();

    }

}
