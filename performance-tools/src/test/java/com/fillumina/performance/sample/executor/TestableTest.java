package com.fillumina.performance.sample.executor;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.util.PerformanceTimeHelper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestableTest {

    private static class TestableCounters implements Testable {
        int setUpCounter, beforeTestCounter, testCounter;

        @Override
        public void setUp() {
            setUpCounter++;
        }

        @Override
        public void onBeforeSample(int iterations) {
            beforeTestCounter++;
        }

        @Override
        public Object test() {
            testCounter++;
            return null;
        }
    }

    @Test
    public void shouldExecuteTheSetUpOnce() {
        final TestableCounters testable = new TestableCounters();
        PerformanceTimerFactory.createSingleThreaded()
                .addTest("test", testable)
                .execute(100);
        assertEquals(1, testable.setUpCounter, 0);
    }

    @Test
    public void shouldExecuteBeforeTestAtEachIteration() {
        final TestableCounters testable = new TestableCounters();
        PerformanceTimerFactory.createSingleThreaded()
                .addTest("test", testable)
                .execute(100);
        assertEquals(1, testable.beforeTestCounter, 0);
    }

    @Test
    public void shouldExecuteTestAtEachIteration() {
        final TestableCounters testable = new TestableCounters();
        PerformanceTimerFactory.createSingleThreaded()
                .addTest("test", testable)
                .execute(100);
        assertEquals(100, testable.testCounter, 0);
    }

    private static class TestableTimer implements Testable {

        @Override
        public void setUp() {
            PerformanceTimeHelper.sleepMicroseconds(7_000);
        }

        @Override
        public void onBeforeSample(int iterations) {
            PerformanceTimeHelper.sleepMicroseconds(3_000);
        }

        @Override
        public Object test() {
            PerformanceTimeHelper.sleepMicroseconds(5_000);
            return null;
        }
    }

    @Test
    public void shouldNotAccountSetupAndBeforeTest() {
        final TestableTimer testable = new TestableTimer();
        PerformanceSample sample = PerformanceTimerFactory.createSingleThreaded()
                .addTest("test", testable)
                .execute(100);
        double time = sample.getTimeMap().get("test").getTimePerIteration();
//        System.out.println("time=" + time);
        assertEquals(5_000_000, time, 10_000); // 0.2% tolerance
    }

}
