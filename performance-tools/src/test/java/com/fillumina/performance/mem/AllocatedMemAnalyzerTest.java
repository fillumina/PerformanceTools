package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
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

    private static final PHolder<MemStats> MEMSTATS =
            AllocatedMemConsumptionExecutor.createMemAnalyzer()
                .addTest(NOMEMORY, new Runnable() {
                    @Override
                    public void run() {
                    }
                })
                .addTest(NOALLOCATED, new Runnable() {
                    @Override
                    public void run() {
                        Sink.drain(new int[10]);
                    }
                })
                .addTest(ALLOCATED, new Runnable() {
                    final List<Object> list = new ArrayList<>(100);
                    @Override
                    public void run() {
                        Sink.drain(list.add(new int[10]));
                    }
                })
                .execute();


    @Test
    public void shouldCheckMultipleAssertion() {
        Assertion<MemStats> assertion = AssertMemory.withTolerance(Ratio.ZERO)
                .assertValue(NOMEMORY).sameAs(0)
                .assertValue(NOALLOCATED).sameAs(0)
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10)
                .assertOrder(NOMEMORY).sameAs(NOALLOCATED)
                .assertOrder(NOMEMORY).lessThan(ALLOCATED);

        MEMSTATS.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        Assertion<MemStats> assertion =
                AssertMemory.withTolerance(Ratio.percentage(10))
                .assertOrder(NOALLOCATED).sameAs(ALLOCATED);

        MEMSTATS.check(assertion);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        Assertion<MemStats> assertion =
                AssertMemory.withTolerance(Ratio.percentage(10))
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10 + 1);

        MEMSTATS.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        Assertion<MemStats> assertion =
                AssertMemory.withTolerance(Ratio.percentage(10))
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10 + 10);

        MEMSTATS.check(assertion);
    }
}
