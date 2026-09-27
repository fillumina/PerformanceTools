package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.IntervalUnit;
import static org.junit.Assert.fail;
import org.junit.Test;

public class ParallelExecutorLivenessTest {
    @Test(timeout = 3000)
    public void shouldRejectInsufficientThreadsRatherThanDeadlock() {
        IndexedHashMap<PathName, Runnable> tests = new IndexedHashMap<>();
        tests.put(PN.pname("parallel"), new ParallelTest().addTask("group", 2, index -> {}));
        try {
            new ParallelMultiThreadPerformanceExecutor(1,
                    IntervalUnit.SECONDS.quantity(1)).executeIterations(tests, new int[]{1});
            fail("a pool with fewer threads than workers cannot start together");
        } catch (IllegalArgumentException expected) {
            // Fail fast instead of waiting for a queued worker indefinitely.
        }
    }
}
