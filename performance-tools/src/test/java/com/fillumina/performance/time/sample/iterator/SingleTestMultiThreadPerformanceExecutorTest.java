package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleTestMultiThreadPerformanceExecutorTest {
    private static final Quantity<IntervalUnit> DAYS_1 =
            IntervalUnit.DAYS.quantity(1);

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectNoTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(1, 1, DAYS_1);
        IndexedHashMap<TName,Runnable> noTest = new IndexedHashMap<>();
        executor.executeIterations(noTest, new int[]{});
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectTwoTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(1, 1, DAYS_1);

        IndexedHashMap<TName,Runnable> testMap = new IndexedHashMap<>();
        testMap.put(TN.tname("one"), (Runnable) () -> {});
        testMap.put(TN.tname("two"), (Runnable) () -> {});

        executor.executeIterations(testMap, new int[]{1, 2});
    }

    @Test
    public void shouldExecuteOneTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(1, 1, DAYS_1);

        IndexedHashMap<TName,Runnable> testMap = new IndexedHashMap<>();
        testMap.put(TN.tname("alpha"), new LfsrRunnable());

        Sample sample = executor
                .executeIterations(testMap, new int[]{1})
                .buildAverageTimeSample();
        Map<TName,SampleValue> timeMap = sample.getValuesMap();

        assertEquals(3, timeMap.size());

        double single = timeMap
                        .get(TN.tname("alpha","single"))
                        .getQuantity().as(AverageTimeUnit.NANOSECONDS);
        double parallel = timeMap
                        .get(TN.tname("alpha", "parallel"))
                        .getQuantity().as(AverageTimeUnit.NANOSECONDS);
        double thread = timeMap
                        .get(TN.tname("alpha", "0"))
                        .getQuantity().as(AverageTimeUnit.NANOSECONDS);

        assertTrue(sample.toString(), thread < single);
        assertTrue(sample.toString(), thread < parallel);
    }
}
