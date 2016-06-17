package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.speed.sample.executor.PerformanceExecutor;
import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.sample.Testable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleThreadPerformanceExecutorTest {

    @Test
    public void shouldExecuteTheTest() {
        final AtomicBoolean executed = new AtomicBoolean(false);
        PerformanceExecutor pe = new SingleThreadPerformanceExecutor(1);
        Map<String,Testable> tests = new LinkedHashMap<>();
        tests.put("single", new AbstractTestable() {
            @Override
            public Object test() {
                executed.set(true);
                return null;
            }
        });
        pe.executeTests(tests, new int[]{1});
        assertTrue(executed.get());
    }

    @Test
    public void shouldExecuteTwoTests() {
        final AtomicBoolean executedOne = new AtomicBoolean(false);
        final AtomicBoolean executedTwo = new AtomicBoolean(false);
        PerformanceExecutor pe = new SingleThreadPerformanceExecutor(1);
        Map<String,Testable> tests = new LinkedHashMap<>();
        tests.put("one", new AbstractTestable() {
            @Override
            public Object test() {
                executedOne.set(true);
                return null;
            }
        });
        tests.put("two", new AbstractTestable() {
            @Override
            public Object test() {
                executedTwo.set(true);
                return null;
            }
        });
        pe.executeTests(tests, new int[]{1, 1});
        assertTrue(executedOne.get());
        assertTrue(executedTwo.get());
    }

    @Test
    public void shouldExecuteATestInFractions() {
        PerformanceExecutor pe = new SingleThreadPerformanceExecutor(3);
        Map<String,Testable> tests = new LinkedHashMap<>();
        tests.put("one", new AbstractTestable() {
            @Override
            public Object test() {
                return null;
            }
        });
        tests.put("two", new AbstractTestable() {
            @Override
            public Object test() {
                return null;
            }
        });

        PerformanceSample sample = pe.executeTests(tests, new int[]{30, 60});

        //assertEquals(30, sample.getTimeMap().get("one").getIterations());
        assertEquals(60, sample.getTimeMap().get("two").getIterations());
    }
}