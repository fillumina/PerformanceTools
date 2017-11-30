package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.test.RunnableSinker;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.util.collection.ArrayMap;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParallelMultiThreadPerformanceExecutorTest {
    private static final Quantity<IntervalUnit> DAYS_1 =
            IntervalUnit.DAYS.quantity(1);

    private static final Runnable NULL_RUNNABLE = () -> {};

    @Test(expected = IllegalArgumentException.class)
    public void shoulNotAcceptRunnableThatAreNotAsymmetricTestable() {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(1, DAYS_1);

        ArrayMap<TName,Runnable> testMap = new ArrayMap<>();

        testMap.put(TN.tname("asymmetric"), new ParallelTest()
                    .addTask("one", 1, NULL_RUNNABLE)
                    .addTask("two", 2, NULL_RUNNABLE));
        testMap.put(TN.tname("not asymmetric"), new Runnable() {
            @Override
            public void run() {
                // do nothing
            }
        });

        executor.executeIterations(testMap, new int[]{250, 250});
    }

    /** The concurrency is adapted automatically to the required level. */
    @Test
    public void shoulAcceptGroupsWithMoreThanConcurrencyLevelElements() {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(2, DAYS_1);

        ArrayMap<TName,Runnable> testMap = new ArrayMap<>();

        testMap.put(TN.tname("asymmetric"), new ParallelTest()
                    .addTask("one", 1, NULL_RUNNABLE)
                    .addTask("two", 2, NULL_RUNNABLE)
                    .addTask("three", 2, NULL_RUNNABLE));

        executor.executeIterations(testMap, new int[]{250});
    }

    @Test
    public void shouldAccountForTheIterationsOfEachAsymmetricWorker() {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(8, DAYS_1);

        ArrayMap<TName,Runnable> testMap = new ArrayMap<>();

        final AtomicInteger oneCounter = new AtomicInteger();
        final AtomicInteger twoCounter = new AtomicInteger();
        int oneWorkers = 2;
        int twoWorkers = 3;
        testMap.put(TN.tname("asymmetric"), new ParallelTest()
                    .addTask("one", oneWorkers, new Runnable() {
                            @Override
                            public void run() {
                                oneCounter.getAndIncrement();
                            }
                        })
                    .addTask("two", twoWorkers, new Runnable() {
                            @Override
                            public void run() {
                                twoCounter.getAndIncrement();
                            }
                        }));

        Sample sample = executor
                .executeIterations(testMap, new int[]{250})
                .buildAverageTimeSample();

//        System.out.println(sample);
//        System.out.println("counter_1=" + oneCounter.get());
//        System.out.println("counter_2=" + twoCounter.get());

        Map<TName,SampleValue> map = sample.getValuesMap();
        assertEquals(map.get(TN.tname("asymmetric", "one")).getIterations(),
                oneCounter.get());
        assertEquals(map.get(TN.tname("asymmetric", "two")).getIterations(),
                twoCounter.get());
    }

    public static void main(final String[] args) {
        DefaultPerformanceTimer pt =
                PerformanceTimerFactory.getMultiThreadedBuilder()
                    .setThreads(8)
                    .buildAsymmetricMultiThreadPerformanceTimer();

        pt.addTest("async", new ParallelTest() {
            private final AtomicInteger counter = new AtomicInteger();
            {
                addTask("inc", 3, new RunnableSinker() {
                    @Override
                    public void run() {
                        drain(counter.getAndIncrement());
                    }
                });
                addTask("get", 1, new RunnableSinker() {
                    @Override
                    public void run() {
                        drain(counter.get());
                    }
                });
            }
        });

        Map<Stats.Type,Sample> resultMap = pt.execute();
        System.out.println("result=" + resultMap.toString());
    }
}
