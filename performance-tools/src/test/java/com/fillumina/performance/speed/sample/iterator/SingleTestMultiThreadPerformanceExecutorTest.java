package com.fillumina.performance.speed.sample.iterator;

import com.fillumina.performance.mock.CountingTestable;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.SpeedSample;
import java.util.LinkedHashMap;
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
        LinkedHashMap<String,Runnable> noTest = new LinkedHashMap<>();
        executor.executeTests(noTest, new int[]{});
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectTwoTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(1, 1, 1,
                        TimeUnit.DAYS);

        LinkedHashMap<String,Runnable> testMap = new LinkedHashMap<>();
        testMap.put("one", new Runnable() { @Override public void run() {} });
        testMap.put("two", new Runnable() { @Override public void run() {} });

        executor.executeTests(testMap, new int[]{1, 2});
    }

    @Test
    public void shouldExecuteOneTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(1, 1, 1,
                        TimeUnit.DAYS);

        LinkedHashMap<String,Runnable> testMap = new LinkedHashMap<>();
        testMap.put("alpha", new CountingTestable());

        SpeedSample sample = executor.executeTests(testMap, new int[]{1});
        Map<String,IterationTime> timeMap = sample.getTimeMap();

        assertEquals(3, timeMap.size());
        assertEquals(1, timeMap.get("alpha_single").getIterations());
        assertEquals(2, timeMap.get("alpha_0").getIterations());
        assertEquals(2, timeMap.get("alpha_parallel").getIterations());
    }
}
