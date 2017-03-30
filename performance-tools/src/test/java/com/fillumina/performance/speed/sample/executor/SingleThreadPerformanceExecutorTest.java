package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.SpeedSample;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
        LinkedHashMap<String,Testable> tests = new LinkedHashMap<>();
        tests.put("single", new Testable() {
            @Override
            public void test() {
                executed.set(true);
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
        LinkedHashMap<String,Testable> tests = new LinkedHashMap<>();
        tests.put("one", new Testable() {
            @Override
            public void test() {
                executedOne.set(true);
            }
        });
        tests.put("two", new Testable() {
            @Override
            public void test() {
                executedTwo.set(true);
            }
        });
        pe.executeTests(tests, new int[]{1, 1});
        assertTrue(executedOne.get());
        assertTrue(executedTwo.get());
    }

    @Test
    public void shouldExecuteATestInFractions() {
        final AtomicInteger t1 = new AtomicInteger();
        final AtomicInteger t2 = new AtomicInteger();
        PerformanceExecutor pe = new SingleThreadPerformanceExecutor(3);
        LinkedHashMap<String,Testable> tests = new LinkedHashMap<>();
        tests.put("one", new Testable() {
            @Override
            public void test() {
                t1.incrementAndGet();
            }
        });
        tests.put("two", new Testable() {
            @Override
            public void test() {
                t2.incrementAndGet();
            }
        });

        SpeedSample sample = pe.executeTests(tests, new int[]{3, 6});

        assertEquals(3, sample.getTimeMap().get("one").getIterations());
        assertEquals(6, sample.getTimeMap().get("two").getIterations());

        assertEquals(3, t1.get());
        assertEquals(6, t2.get());
    }
}