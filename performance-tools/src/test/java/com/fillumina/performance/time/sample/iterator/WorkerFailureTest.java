package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.fail;
import org.junit.Test;

public class WorkerFailureTest {
    @Test
    public void shouldPropagateWorkerFailureInMultiTestExecutor() {
        IndexedHashMap<PathName, Runnable> tests = new IndexedHashMap<>();
        tests.put(PN.pname("broken"), () -> { throw new IllegalStateException("worker failed"); });
        try {
            new MultiThreadPerformanceExecutor(2, 2, IntervalUnit.SECONDS.quantity(2))
                    .executeIterations(tests, new int[]{1});
            fail("worker failure was reported as a timing sample");
        } catch (IllegalStateException expected) {
            // The failed workload must reach the caller.
        }
    }

    @Test
    public void shouldPropagateWorkerFailureInSingleTestExecutor() {
        AtomicInteger calls = new AtomicInteger();
        IndexedHashMap<PathName, Runnable> tests = new IndexedHashMap<>();
        tests.put(PN.pname("broken"), () -> {
            if (calls.incrementAndGet() > 1) {
                throw new IllegalStateException("worker failed");
            }
        });
        try {
            new SingleTestMultiThreadPerformanceExecutor(2, 2,
                    IntervalUnit.SECONDS.quantity(2)).executeIterations(tests, new int[]{1});
            fail("worker failure was reported as a timing sample");
        } catch (IllegalStateException expected) {
            // The first, single-threaded invocation succeeds; a worker fails.
        }
    }

    @Test
    public void shouldPropagateWorkerFailureInTimedParallelExecutor() {
        IndexedHashMap<PathName, Runnable> tests = new IndexedHashMap<>();
        tests.put(PN.pname("broken"), new ParallelTest().addTask("group", 2,
                index -> { throw new IllegalStateException("worker failed"); }));
        try {
            new ParallelMultiThreadPerformanceExecutor(2,
                    IntervalUnit.SECONDS.quantity(1)).executeIterations(tests, new int[]{0});
            fail("worker failure was reported as a timing sample");
        } catch (IllegalStateException expected) {
            // Timed, zero-iteration execution must also propagate worker failures.
        }
    }

    @Test
    public void shouldPropagateWorkerFailureInParallelExecutor() {
        IndexedHashMap<PathName, Runnable> tests = new IndexedHashMap<>();
        tests.put(PN.pname("broken"), new ParallelTest().addTask("group", 2,
                index -> { throw new IllegalStateException("worker failed"); }));
        try {
            new ParallelMultiThreadPerformanceExecutor(2,
                    IntervalUnit.SECONDS.quantity(2)).executeIterations(tests, new int[]{1});
            fail("worker failure was reported as a timing sample");
        } catch (IllegalStateException expected) {
            // A parallel group must not hide its failed workers.
        }
    }
}
