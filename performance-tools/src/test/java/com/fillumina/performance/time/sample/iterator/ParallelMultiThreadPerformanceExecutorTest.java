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

    // TODO this test fails a lot during mvn clean install
    @Test
    public void shouldAccountForTheIterationsOfEachAsymmetricWorker() {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(1, DAYS_1);

        IndexedHashMap<TName,Runnable> testMap = new IndexedHashMap<>();

        AtomicInteger aCounter = new AtomicInteger();
        AtomicInteger bCounter = new AtomicInteger();

        int aWorkers = 1;
        int bWorkers = 3;

        testMap.put(TN.tname("asymmetric"),
                new ParallelTest()
                    .addTask("a", aWorkers, () -> aCounter.getAndIncrement() )
                    .addTask("b", bWorkers, () -> bCounter.getAndIncrement() ) );

        int[] iterations = new int[]{10_000};

        executor.executeIterations(testMap, iterations);

        double aNormalizedResult = aCounter.get() / aWorkers;
        double bNormalizedResult = bCounter.get() / bWorkers;

        // it's huge, I understand
        double error = bNormalizedResult * 0.25;

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
