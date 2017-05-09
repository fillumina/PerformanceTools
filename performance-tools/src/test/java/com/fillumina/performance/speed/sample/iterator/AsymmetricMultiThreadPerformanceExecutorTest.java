package com.fillumina.performance.speed.sample.iterator;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.RunnableSinker;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AsymmetricMultiThreadPerformanceExecutorTest {

    private static final Runnable NULL_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            // do nothing
        }
    };

    @Test(expected = IllegalArgumentException.class)
    public void shoulNotAcceptRunnableThatAreNotAsymmetricTestable() {
        AsymmetricMultiThreadPerformanceExecutor executor =
                new AsymmetricMultiThreadPerformanceExecutor(1, 1, TimeUnit.DAYS);

        LinkedHashMap<TName,Runnable> testMap = new LinkedHashMap<>();

        testMap.put(TN.tname("asymmetric"), new AsymmetricTestable()
                    .addGroup("one", 1, NULL_RUNNABLE)
                    .addGroup("two", 2, NULL_RUNNABLE));
        testMap.put(TN.tname("not asymmetric"), new Runnable() {
            @Override
            public void run() {
                // do nothing
            }
        });

        executor.executeTests(testMap, new int[]{250, 250});
    }

    @Test(expected = IllegalArgumentException.class)
    public void shoulNotAcceptGroupsWithMoreThanConcurrencyLevelElements() {
        AsymmetricMultiThreadPerformanceExecutor executor =
                new AsymmetricMultiThreadPerformanceExecutor(2, 1, TimeUnit.DAYS);

        LinkedHashMap<TName,Runnable> testMap = new LinkedHashMap<>();

        testMap.put(TN.tname("asymmetric"), new AsymmetricTestable()
                    .addGroup("one", 1, NULL_RUNNABLE)
                    .addGroup("two", 2, NULL_RUNNABLE)
                    .addGroup("three", 2, NULL_RUNNABLE));

        executor.executeTests(testMap, new int[]{250});
    }

    @Test
    public void shouldAccountForTheIterationsOfEachAsymmetricWorker() {
        AsymmetricMultiThreadPerformanceExecutor executor =
                new AsymmetricMultiThreadPerformanceExecutor(8, 1, TimeUnit.DAYS);

        LinkedHashMap<TName,Runnable> testMap = new LinkedHashMap<>();

        final AtomicInteger oneCounter = new AtomicInteger();
        final AtomicInteger twoCounter = new AtomicInteger();
        int oneWorkers = 2;
        int twoWorkers = 3;
        testMap.put(TN.tname("asymmetric"), new AsymmetricTestable()
                    .addGroup("one", oneWorkers, new Runnable() {
                            @Override
                            public void run() {
                                oneCounter.getAndIncrement();
                            }
                        })
                    .addGroup("two", twoWorkers, new Runnable() {
                            @Override
                            public void run() {
                                twoCounter.getAndIncrement();
                            }
                        }));

        SpeedSample sample = executor.executeTests(testMap, new int[]{250});

//        System.out.println(sample);
//        System.out.println("counter_1=" + oneCounter.get());
//        System.out.println("counter_2=" + twoCounter.get());

        Map<TName,IterationTime> map = sample.getTimeMap();
        assertEquals(map.get(TN.tname("asymmetric", "one", "2")).getIterations(),
                oneCounter.get());
        assertEquals(map.get(TN.tname("asymmetric", "two", "3")).getIterations(),
                twoCounter.get());
    }

    public static void main(final String[] args) {
        DefaultPerformanceTimer pt =
                PerformanceTimerFactory.getMultiThreadedBuilder()
                    .setThreads(8)
                    .buildAsymmetricMultiThreadPerformanceTimer();

        pt.addTest("aync", new AsymmetricTestable() {
            private final AtomicInteger counter = new AtomicInteger();
            {
                addGroup("inc", 3, new RunnableSinker() {
                    @Override
                    public void run() {
                        drain(counter.getAndIncrement());
                    }
                });
                addGroup("get", 1, new RunnableSinker() {
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
