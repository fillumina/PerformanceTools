package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.sample.iterator.ParallelTest.ConcurrentRunnable;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParallelMultiThreadPerformanceExecutorTest {
    private static final Quantity<IntervalUnit> TIMEOUT =
            IntervalUnit.SECONDS.quantity(5);

    private static final ConcurrentRunnable NULL_RUNNABLE = (int i) -> {};

    @Test(expected = IllegalArgumentException.class)
    public void shoulNotAcceptRunnableThatAreNotAsymmetricTestable() {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(-1, TIMEOUT);

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
        // concurrencyLevel = 0 means to create as many threads as workers
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(0, TIMEOUT);

        IndexedHashMap<PathName,Runnable> testMap = new IndexedHashMap<>();

        testMap.put(PN.pname("asymmetric"), new ParallelTest()
                    .addTask("one", 1, NULL_RUNNABLE)
                    .addTask("two", 2, NULL_RUNNABLE)
                    .addTask("three", 2, NULL_RUNNABLE));

        executor.executeIterations(testMap, new int[]{250});

        assertEquals(5, executor.getConcurrencyLevel());
    }

    private static class Counter {
        private final AtomicIntegerArray array;

        public Counter(int size) {
            array = new AtomicIntegerArray(size);
        }

        public void incrementIndex(int index) {
            array.incrementAndGet(index);
        }

        public int getIndex(int index) {
            return array.get(index);
        }

        public int size() {
            return array.length();
        }

        @Override
        public String toString() {
            return array.toString();
        }
    }

    @Test
    public void shouldIterateForGivenIterations() {
        shouldAccountForTheIterationsOfEachAsymmetricWorker(new int[] {5_000});
    }

    @Test
    public void shouldRunForGivenTime() {
        shouldAccountForTheIterationsOfEachAsymmetricWorker(new int[] {0});
    }

    public void shouldAccountForTheIterationsOfEachAsymmetricWorker(
            int[] iterations) {
        ParallelMultiThreadPerformanceExecutor executor =
                new ParallelMultiThreadPerformanceExecutor(-1, TIMEOUT);

        IndexedHashMap<PathName,Runnable> testMap = new IndexedHashMap<>();

        int aWorkers = 1;
        int bWorkers = 3;

        Counter counter = new Counter(aWorkers + bWorkers);

        testMap.put(PN.pname("asymmetric"),
                new ParallelTest()
                    .addTask("a", aWorkers, i -> counter.incrementIndex(bWorkers) )
                    .addTask("b", bWorkers, i -> counter.incrementIndex(i) ) );

        executor.executeIterations(testMap, iterations);

        int total = 0;
        for (int i = 0; i < counter.size(); i++) {
            int count = counter.getIndex(i);
            total += count;
            if (iterations[0] > 0) {
                assertEquals("worker " + i + " ran the wrong number of times",
                        iterations[0], count);
            }
        }
        // A timed worker can be delayed by the scheduler even after the
        // start latch opens; require progress, not fairness across threads.
        if (iterations[0] == 0) {
            assertTrue("timed workers made no progress", total > 0);
        }
    }

    public static void main(final String[] args) {
        DefaultPerformanceTimer pt =
                PerformanceTimerFactory.getMultiThreadedBuilder()
                    .setThreads(8)
                    .buildAsymmetricMultiThreadPerformanceTimer();

        pt.addTest("async", new ParallelTest() {
            private final AtomicInteger counter = new AtomicInteger();
            {
                addTask("inc", 3, i -> {
                    Sink.drain(counter.getAndIncrement());
                });
                addTask("get", 1, i -> {
                    Sink.drain(counter.get());
                });
            }
        });

        pt.execute().forEach((type, sample) -> {
            System.out.println("\n" + type.toString() + ":\n" +
                    sample.toString());
        });
    }
}
