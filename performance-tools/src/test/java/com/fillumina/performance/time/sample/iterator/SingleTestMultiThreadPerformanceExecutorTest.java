package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Map;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
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
        IndexedHashMap<PathName,Runnable> noTest = new IndexedHashMap<>();
        executor.executeIterations(noTest, new int[]{});
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectTwoTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(1, 1, DAYS_1);

        IndexedHashMap<PathName,Runnable> testMap = new IndexedHashMap<>();
        testMap.put(PN.pname("one"), (Runnable) () -> {});
        testMap.put(PN.pname("two"), (Runnable) () -> {});

        executor.executeIterations(testMap, new int[]{1, 2});
    }

    @Test
    public void shouldExecuteOneTests() {
        SingleTestMultiThreadPerformanceExecutor executor =
                new SingleTestMultiThreadPerformanceExecutor(2, 2, DAYS_1);

        IndexedHashMap<PathName,Runnable> testMap = new IndexedHashMap<>();
        testMap.put(PN.pname("alpha"), new LfsrRunnable());

        Sample sample = executor
                .executeIterations(testMap, new int[]{1})
                .buildAverageTimeSample();
        Map<PathName,SampleValue> timeMap = sample.getValuesMap();

        assertEquals(4, timeMap.size());

        // peformance of the test executed in single thread
        double single = timeMap
                        .get(PN.pname("alpha", "single"))
                        .getQuantity().as(AverageTimeUnit.NANOSECONDS);

        // performance of the test excecuted in parallel
        double parallel = timeMap
                        .get(PN.pname("alpha", "parallel"))
                        .getQuantity().as(AverageTimeUnit.NANOSECONDS);

        // performnace of a single thread while executed in parallel
        double thread0 = timeMap
                        .get(PN.pname("alpha", "0"))
                        .getQuantity().as(AverageTimeUnit.NANOSECONDS);
        double thread1 = timeMap
                        .get(PN.pname("alpha", "1"))
                        .getQuantity().as(AverageTimeUnit.NANOSECONDS);

        Assert.assertNotEquals(0, single);
        Assert.assertNotEquals(0, thread0);
        Assert.assertNotEquals(0, thread1);
        Assert.assertNotEquals(0, parallel);

    }
}
