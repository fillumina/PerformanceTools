package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.mem.stats.AllocatedMemStats;
import com.fillumina.performance.mem.stats.OLD_MemStatsProducer;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemAnalyzerTest {
    private static final String NOMEMORY = "nomemory";
    private static final String NOALLOCATED = "noallocated";
    private static final String ALLOCATED = "allocated";

    private static final AllocatedMemStats MEMSTATS =
            OLD_MemStatsProducer.createAllocated()
                .addTest(NOMEMORY, new LfsrRunnable())
                .addTest(NOALLOCATED, () -> { SafeSink.drain(new int[10]); })
                .addTest(ALLOCATED, new Runnable() {
                    final List<Object> list = new ArrayList<>(100);
                    @Override
                    public void run() {
                        SafeSink.drain(list.add(new int[10]));
                    }
                })
                .execute()
                .getStats(AllocatedMemStats.class)
                .getAssertable();


    @Test
    public void shouldCheckMultipleAssertion() {
        Assertion assertion = Assertions.withTolerance(Ratio.ZERO)
                .assertValue(NOMEMORY).sameAs(0)
                .assertValue(NOALLOCATED).sameAs(0)
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10)
                .assertOrder(NOMEMORY).sameAs(NOALLOCATED)
                .assertOrder(NOMEMORY).lessThan(ALLOCATED);

        assertion.check(MEMSTATS);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        Assertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertOrder(NOALLOCATED).sameAs(ALLOCATED);

        assertion.check(MEMSTATS);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        Assertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10 + 1);

        assertion.check(MEMSTATS);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        Assertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10 + 10);

        assertion.check(MEMSTATS);
    }
}
