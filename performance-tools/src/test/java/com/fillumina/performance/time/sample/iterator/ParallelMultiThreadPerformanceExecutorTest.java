package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.test.RunnableSinker;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
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

        IndexedHashMap<PathName,Runnable> testMap = new IndexedHashMap<>();

        testMap.put(PN.pname("asymmetric"), new ParallelTest()
                    .addTask("one", 1, NULL_RUNNABLE)
                    .addTask("two", 2, NULL_RUNNABLE));
        testMap.put(PN.pname("not asymmetric"), new Runnable() {
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

        IndexedHashMap<PathName,Runnable> testMap = new IndexedHashMap<>();

        testMap.put(PN.pname("asymmetric"), new ParallelTest()
                    .addTask("one", 1, NULL_RUNNABLE)
                    .addTask("two", 2, NULL_RUNNABLE)
                    .addTask("three", 2, NULL_RUNNABLE));

        executor.executeIterations(testMap, new int[]{250});

        assertEquals(5, executor.getConcurrencyLevel());
    }

    private static class ThreadLocalCounter {
        private int[] array = new int[128];

        public void increment() {
            final int thread = Thread.currentThread().hashCode();
            // an unfortunate clash could obviously happen
            array[thread % 128]++;
        }

        public int size() {
            int counter = 0;
            for (int v : array) {
                if (v != 0) {
                    counter++;
                }
            }
            return counter;
        }

        public int getTotal() {
            int counter = 0;
            for (int v : array) {
                counter += v;
            }
            return counter;
        }
    }

    @Test
    public void shouldAccountForTheIterationsOfEachAsymmetricWorker() {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(1, DAYS_1);

        IndexedHashMap<PathName,Runnable> testMap = new IndexedHashMap<>();

        ThreadLocalCounter aCounter = new ThreadLocalCounter();
        ThreadLocalCounter bCounter = new ThreadLocalCounter();

        int aWorkers = 1;
        int bWorkers = 3;

        testMap.put(PN.pname("asymmetric"),
                new ParallelTest()
                    .addTask("a", aWorkers, () -> aCounter.increment() )
                    .addTask("b", bWorkers, () -> bCounter.increment() ) );

        int[] iterations = new int[] {5_000};

        TimeSampleBuilder builder =
                executor.executeIterations(testMap, iterations);

        //System.out.println(builder.buildAverageTimeSample().toString());

        double aNormalizedResult = aCounter.getTotal() / aWorkers;
        double bNormalizedResult = bCounter.getTotal() / bWorkers;

        // it's huge, I understand
        double error = bNormalizedResult * 0.25;

        assertEquals("hashcode clash: don't worry, repeat test",
                aWorkers, aCounter.size());
        assertEquals("hashcode clash: don't worry, repeat test",
                bWorkers, bCounter.size());

        assertEquals("count_1=" + aNormalizedResult +
                    ", count_2=" + bNormalizedResult +
                    ", error=" + error,
                aNormalizedResult, bNormalizedResult, error);
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

        pt.execute().forEach((type, sample) -> {
            System.out.println("\n" + type.toString() + ":\n" +
                    sample.toString());
        });
    }
}
