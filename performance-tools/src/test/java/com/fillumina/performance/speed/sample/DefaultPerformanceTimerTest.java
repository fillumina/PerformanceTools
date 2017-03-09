package com.fillumina.performance.speed.sample;

import com.fillumina.performance.mock.MockPerformanceCreator;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.executor.PerformanceExecutor;
import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.testable.NullTestable;
import java.util.Map;
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
        pt.addTest("one", NullTestable.INSTANCE);
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
        SpeedSample sample = pt.execute().getStats();
        assertTrue(sample.getTimeMap().get("one").getIterations() > 0);
    }

    @Test
    public void shouldExecuteATestWithTheGivenNumberOfIterations() {
        SpeedSample sample = new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            Map<String, Testable> tests, int[] iterations) {
                        return MockPerformanceCreator.createSample(iterations[0],
                                new Object[][]{{"one", 100}});
                    }
                })
                .addTest("test", NullTestable.INSTANCE)
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
                            return MockPerformanceCreator.createSample(iterations[0],
                                new Object[][]{{"one", 250}});
                        }
                    })
                .addTest("test", NullTestable.INSTANCE)
                .iterationTimeEstimator(25);
        assertEquals(iterations[0], iterationCounter.get());
    }

    @Test
    public void shouldDispatchTheSampleToConsumers() {
        final AtomicBoolean dispatched = new AtomicBoolean(false);
        final SpeedSample sample = MockPerformanceCreator.createSample(123,
                        new Object[][]{{"single", 666}});
        new DefaultPerformanceTimer(
                new PerformanceExecutor() {
                    @Override
                    public SpeedSample executeTests(
                            Map<String, Testable> tests, int[] iterations) {
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

    // JVM isn't that aggressive on optimizations when testing
    @Ignore @Test(expected = RuntimeException.class)
    public void shouldDetectCodeEviction() {
        System.out.println("runtime");
        PerformanceTimerFactory.createSingleThreaded()
                .addTest("test", NullTestable.INSTANCE)
                .iterationTimeEstimator(25);
    }

    public static void main(final String[] args) {

        PerformanceTimerFactory.createSingleThreaded()
                .addPerformanceConsumer(SampleLineStringGenerator.VIEWER)
                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                            .setMaxPercentageMargin(3)
                            .build())
                .addTest("null", NullTestable.INSTANCE)
                .addPerformanceConsumer(WrapperSpeedStatsTableStringGenerator.VIEWER)

                .execute()
                .print();

    }

}
