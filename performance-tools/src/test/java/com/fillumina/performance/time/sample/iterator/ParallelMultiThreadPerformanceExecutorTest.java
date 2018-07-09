package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.test.RunnableSinker;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.tname.TName;
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

        IndexedHashMap<TName,Runnable> testMap = new IndexedHashMap<>();

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

        IndexedHashMap<TName,Runnable> testMap = new IndexedHashMap<>();

        testMap.put(TN.tname("asymmetric"), new ParallelTest()
                    .addTask("one", 1, NULL_RUNNABLE)
                    .addTask("two", 2, NULL_RUNNABLE)
                    .addTask("three", 2, NULL_RUNNABLE));

        executor.executeIterations(testMap, new int[]{250});

        assertEquals(5, executor.getConcurrencyLevel());
    }

    @Test
    public void shouldAccountForTheIterationsOfEachAsymmetricWorker() {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(8, DAYS_1);

        IndexedHashMap<TName,Runnable> testMap = new IndexedHashMap<>();

        final AtomicInteger aCounter = new AtomicInteger();
        final AtomicInteger bCounter = new AtomicInteger();
        int aWorkers = 1;
        int bWorkers = 3;
        testMap.put(TN.tname("asymmetric"), new ParallelTest()
                    .addTask("one", aWorkers,
                            () -> aCounter.getAndIncrement() )
                    .addTask("two", bWorkers,
                            () -> bCounter.getAndIncrement() ) );

        executor.executeIterations(testMap, new int[]{250});

        // the tests will be executed in parallel
        assertEquals("count_1=" + bWorkers * aCounter.get() +
                    ", count_2=" + aWorkers * bCounter.get(),
                bWorkers * aCounter.get(), aWorkers * bCounter.get(),
                aWorkers * bCounter.get() * 0.1);
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
            System.out.println("\n" + type.toString() + ":\n" + sample.toString());
        });
    }
}
