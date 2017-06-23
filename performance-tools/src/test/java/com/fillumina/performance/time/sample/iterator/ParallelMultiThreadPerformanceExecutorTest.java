package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.infrastructure.RunnableSinker;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParallelMultiThreadPerformanceExecutorTest {

    private static final Runnable NULL_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            // do nothing
        }
    };

    @Test(expected = IllegalArgumentException.class)
    public void shoulNotAcceptRunnableThatAreNotAsymmetricTestable() {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(1, 1, TimeUnit.DAYS);

        LinkedMap<TName,Runnable> testMap = new LinkedMap<>();

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
                new ParallelMultiThreadPerformanceExecutor(2, 1, TimeUnit.DAYS);

        LinkedMap<TName,Runnable> testMap = new LinkedMap<>();

        testMap.put(TN.tname("asymmetric"), new ParallelTest()
                    .addTask("one", 1, NULL_RUNNABLE)
                    .addTask("two", 2, NULL_RUNNABLE)
                    .addTask("three", 2, NULL_RUNNABLE));

        executor.executeIterations(testMap, new int[]{250});
    }

    @Test
    public void shouldAccountForTheIterationsOfEachAsymmetricWorker() {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(8, 1, TimeUnit.DAYS);

        LinkedMap<TName,Runnable> testMap = new LinkedMap<>();

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

        TimeSample sample = executor.executeIterations(testMap, new int[]{250});

//        System.out.println(sample);
//        System.out.println("counter_1=" + oneCounter.get());
//        System.out.println("counter_2=" + twoCounter.get());

        Map<TName,IterationTime> map = sample.getTimeMap();
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

        pt.addTest("aync", new ParallelTest() {
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

        pt.execute().print();

    }
}
