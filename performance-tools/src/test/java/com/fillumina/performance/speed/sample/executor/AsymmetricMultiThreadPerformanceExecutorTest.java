package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.SpeedSample;
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
    public void shoulNotAcceptTestableThatAreNotAsymmetricTestable() {
        AsymmetricMultiThreadPerformanceExecutor executor =
                new AsymmetricMultiThreadPerformanceExecutor(1, 1, TimeUnit.DAYS);

        LinkedHashMap<String,Testable> testMap = new LinkedHashMap<>();

        testMap.put("asymmetric", new AsymmetricTestable()
                    .addGroup("one", 1, NULL_RUNNABLE)
                    .addGroup("two", 2, NULL_RUNNABLE));
        testMap.put("not asymmetric", new Testable() {
            @Override
            public void test() {
                // do nothing
            }
        });

        executor.executeTests(testMap, new int[]{250, 250});
    }

    @Test(expected = IllegalArgumentException.class)
    public void shoulNotAcceptGroupsWithMoreThanConcurrencyLevelElements() {
        AsymmetricMultiThreadPerformanceExecutor executor =
                new AsymmetricMultiThreadPerformanceExecutor(2, 1, TimeUnit.DAYS);

        LinkedHashMap<String,Testable> testMap = new LinkedHashMap<>();

        testMap.put("asymmetric", new AsymmetricTestable()
                    .addGroup("one", 1, NULL_RUNNABLE)
                    .addGroup("two", 2, NULL_RUNNABLE)
                    .addGroup("three", 2, NULL_RUNNABLE));

        executor.executeTests(testMap, new int[]{250});
    }

    @Test
    public void shouldAccountForTheIterationsOfEachAsymmetricWorker() {
        AsymmetricMultiThreadPerformanceExecutor executor =
                new AsymmetricMultiThreadPerformanceExecutor(8, 1, TimeUnit.DAYS);

        LinkedHashMap<String,Testable> testMap = new LinkedHashMap<>();

        final AtomicInteger oneCounter = new AtomicInteger();
        final AtomicInteger twoCounter = new AtomicInteger();
        int oneWorkers = 2;
        int twoWorkers = 3;
        testMap.put("asymmetric", new AsymmetricTestable()
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

        Map<String,IterationTime> map = sample.getTimeMap();
        assertEquals(
                map.get("asymmetric_one_2").getIterations(),
                oneCounter.get());
        assertEquals(
                map.get("asymmetric_two_3").getIterations(),
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
