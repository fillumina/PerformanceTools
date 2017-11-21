package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.tname.TName;
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
        tests.put(TN.tname("single"), () -> executed.set(true) );
        pe.executeIterations(tests, new int[]{1});
        assertTrue(executed.get());
    }

    @Test
    public void shouldExecuteTwoTests() {
        final AtomicBoolean executedOne = new AtomicBoolean(false);
        final AtomicBoolean executedTwo = new AtomicBoolean(false);
        PerformanceExecutor pe = new SingleThreadPerformanceExecutor(1);
        LinkedMap<TName,Runnable> tests = new LinkedMap<>();
        tests.put(ONE, () -> executedOne.set(true) );
        tests.put(TWO, () -> executedTwo.set(true) );
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
        tests.put(ONE, () -> t1.incrementAndGet() );
        tests.put(TWO, () -> t2.incrementAndGet() );

        Sample sample = pe.executeIterations(tests, new int[]{3, 6})
                .buildAverageTimeSample();

        assertEquals(3, sample.getValuesMap().get(ONE).getIterations());
        assertEquals(6, sample.getValuesMap().get(TWO).getIterations());

        assertEquals(3, t1.get());
        assertEquals(6, t2.get());
    }
}