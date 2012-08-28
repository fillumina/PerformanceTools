package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class MultiThreadPerformanceTimerTest {

    private boolean printOut = false;

    public static void main(final String[] args) {
        final MultiThreadPerformanceTimerTest test =
                new MultiThreadPerformanceTimerTest();
        test.printOut = true;
        test.shouldUseDifferentThreads();
    }

    @Test
    public void shouldUseDifferentThreads() {

        executeMultiThreadedTest(8, 8, 32);
        executeMultiThreadedTest(1, 8, 32);
        executeMultiThreadedTest(8, 1, 32);
        executeMultiThreadedTest(1, 1, 32);
    }

    private void executeMultiThreadedTest(final int concurrency,
            final int workerNumber,
            final int iterations) {
        PerformanceTimer pt = PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(concurrency)
                .setWorkerNumber(workerNumber)
                .setTimeout(5, TimeUnit.SECONDS)
                .build();

        final AtomicInteger objcetIndex = new AtomicInteger();

        final Map<Thread, Integer> threadCounterMap =
                new ConcurrentHashMap<>();

        final Queue<Integer> counterSequence = new ConcurrentLinkedQueue<>();

        pt.addTest("alfa", new Runnable() {
            final int threadNumber = objcetIndex.incrementAndGet();
            final AtomicInteger index = new AtomicInteger();

            @Override
            public void run() {
                incrementThreadOccurrenceCounter();
                counterSequence.add(index.incrementAndGet());
                printOutInfo();
            }

            private void incrementThreadOccurrenceCounter() {
                final Thread currentThread = Thread.currentThread();
                Integer counter = threadCounterMap.get(currentThread);
                if (counter == null) {
                    counter = 0;
                }
                threadCounterMap.put(currentThread, counter++);
            }

            private void printOutInfo() {
                if (printOut) {
                    System.out.println(this + " - " +
                            Thread.currentThread() + " " +
                            threadNumber + " " + index);
                }
            }
        });

        pt.iterate(iterations);

        // there were as many threads as the maximum allowed
        assertEquals(Math.min(concurrency, workerNumber), threadCounterMap.size());

        // all iterations
        assertTrue(isSequence(counterSequence));

        assertEquals(workerNumber * iterations, counterSequence.size());
    }

    private boolean isSequence(final Queue<Integer> queue) {
        final List<Integer> list = new ArrayList<>(queue);
        Collections.sort(list);
        int index = 1;
        for (Integer i: list) {
            if (i != index) {
                System.out.println(i + " != " + index);
                return false;
            }
            index++;
        }
        return true;
    }
}
