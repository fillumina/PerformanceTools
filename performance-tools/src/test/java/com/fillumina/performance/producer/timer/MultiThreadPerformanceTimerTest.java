package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerFactory;
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
 * Assesses if a multi threaded performance test is executed as expected.
 *
 * @author Francesco Illuminati
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

    private void executeMultiThreadedTest(final int threads,
            final int workers,
            final int iterations) {
        PerformanceTimer pt = PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(threads)
                .setWorkers(workers)
                .setTimeout(5, TimeUnit.SECONDS)
                .build();

        final AtomicInteger objectCounter = new AtomicInteger();

        final Map<Thread, Integer> threadCallCounterMap =
                new ConcurrentHashMap<>();

        final Queue<Integer> codeExecutionCounter =
                new ConcurrentLinkedQueue<>();

        pt.addTest("alfa", new Runnable() {
            {
                objectCounter.incrementAndGet();
            }
            final AtomicInteger index = new AtomicInteger();

            @Override
            public void run() {
                incrementThreadOccurrenceCounter();
                codeExecutionCounter.add(index.incrementAndGet());
                printOutInfo();
            }

            private void incrementThreadOccurrenceCounter() {
                final Thread currentThread = Thread.currentThread();
                Integer counter = threadCallCounterMap.get(currentThread);
                if (counter == null) {
                    counter = 0;
                }
                threadCallCounterMap.put(currentThread, counter++);
            }

            private void printOutInfo() {
                if (printOut) {
                    System.out.println(this + " - " +
                            Thread.currentThread() + " " + index);
                }
            }
        });

        pt.iterate(iterations);

        // there is only one Object executed by many workers in many threads
        assertEquals(1, objectCounter.get());

        // there were as many threads as the maximum allowed
        assertEquals(Math.min(threads, workers), threadCallCounterMap.size());

        // all iterations
        assertEquals(iterations * workers, codeExecutionCounter.size());

        // the executed code is always the same Runnable inner class
        assertTrue(isSequence(codeExecutionCounter));
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
