package com.fillumina.performance.speed.sample.iterator;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.TName;
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
    private static final TName TWO = TN.n("two");
    private static final TName ONE = TN.n("one");

    @Test
    public void shouldExecuteTheTest() {
        final AtomicBoolean executed = new AtomicBoolean(false);
        PerformanceExecutor pe = new SingleThreadPerformanceExecutor(1);
        LinkedHashMap<TName,Runnable> tests = new LinkedHashMap<>();
        tests.put(TN.n("single"), new Runnable() {
            @Override
            public void run() {
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
        LinkedHashMap<TName,Runnable> tests = new LinkedHashMap<>();
        tests.put(ONE, new Runnable() {
            @Override
            public void run() {
                executedOne.set(true);
            }
        });
        tests.put(TWO, new Runnable() {
            @Override
            public void run() {
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
        LinkedHashMap<TName,Runnable> tests = new LinkedHashMap<>();
        tests.put(ONE, new Runnable() {
            @Override
            public void run() {
                t1.incrementAndGet();
            }
        });
        tests.put(TWO, new Runnable() {
            @Override
            public void run() {
                t2.incrementAndGet();
            }
        });

        SpeedSample sample = pe.executeTests(tests, new int[]{3, 6});

        assertEquals(3, sample.getTimeMap().get(ONE).getIterations());
        assertEquals(6, sample.getTimeMap().get(TWO).getIterations());

        assertEquals(3, t1.get());
        assertEquals(6, t2.get());
    }
}