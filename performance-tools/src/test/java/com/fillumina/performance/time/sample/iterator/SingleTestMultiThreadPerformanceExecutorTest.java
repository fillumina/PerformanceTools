package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.time.sample.iterator.SingleTestMultiThreadPerformanceExecutor;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.CountingTestable;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.time.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleTestMultiThreadPerformanceExecutorTest {

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectNoTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(1, 1, 1,
                        TimeUnit.DAYS);
        LinkedMap<TName,Runnable> noTest = new LinkedMap<>();
        executor.executeIterations(noTest, new int[]{});
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectTwoTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(1, 1, 1,
                        TimeUnit.DAYS);

        LinkedMap<TName,Runnable> testMap = new LinkedMap<>();
        testMap.put(TN.tname("one"), (Runnable) () -> {});
        testMap.put(TN.tname("two"), (Runnable) () -> {});

        executor.executeIterations(testMap, new int[]{1, 2});
    }

    @Test
    public void shouldExecuteOneTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(1, 1, 1,
                        TimeUnit.DAYS);

        LinkedMap<TName,Runnable> testMap = new LinkedMap<>();
        testMap.put(TN.tname("alpha"), new CountingTestable());

        SpeedSample sample = executor.executeIterations(testMap, new int[]{1});
        Map<TName,IterationTime> timeMap = sample.getTimeMap();

        assertEquals(3, timeMap.size());
        assertEquals(1, timeMap.get(TN.tname("alpha","single")).getIterations());
        assertEquals(2, timeMap.get(TN.tname("alpha", "0")).getIterations());
        assertEquals(2, timeMap.get(TN.tname("alpha", "parallel")).getIterations());
    }
}
