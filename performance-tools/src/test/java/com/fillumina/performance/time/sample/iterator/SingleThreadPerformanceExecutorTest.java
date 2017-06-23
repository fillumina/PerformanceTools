package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.time.sample.iterator.SingleThreadPerformanceExecutor;
import com.fillumina.performance.time.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.time.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
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
    private static final TName TWO = TN.tname("two");
    private static final TName ONE = TN.tname("one");

    @Test
    public void shouldExecuteTheTest() {
        final AtomicBoolean executed = new AtomicBoolean(false);
        PerformanceExecutor pe = new SingleThreadPerformanceExecutor(1);
        LinkedMap<TName,Runnable> tests = new LinkedMap<>();
        tests.put(TN.tname("single"), new Runnable() {
            @Override
            public void run() {
                executed.set(true);
            }
        });
        pe.executeIterations(tests, new int[]{1});
        assertTrue(executed.get());
    }

    @Test
    public void shouldExecuteTwoTests() {
        final AtomicBoolean executedOne = new AtomicBoolean(false);
        final AtomicBoolean executedTwo = new AtomicBoolean(false);
        PerformanceExecutor pe = new SingleThreadPerformanceExecutor(1);
        LinkedMap<TName,Runnable> tests = new LinkedMap<>();
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
        pe.executeIterations(tests, new int[]{1, 1});
        assertTrue(executedOne.get());
        assertTrue(executedTwo.get());
    }

    @Test
    public void shouldExecuteATestInFractions() {
        final AtomicInteger t1 = new AtomicInteger();
        final AtomicInteger t2 = new AtomicInteger();
        PerformanceExecutor pe = new SingleThreadPerformanceExecutor(3);
        LinkedMap<TName,Runnable> tests = new LinkedMap<>();
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

        SpeedSample sample = pe.executeIterations(tests, new int[]{3, 6});

        assertEquals(3, sample.getTimeMap().get(ONE).getIterations());
        assertEquals(6, sample.getTimeMap().get(TWO).getIterations());

        assertEquals(3, t1.get());
        assertEquals(6, t2.get());
    }
}