package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.assertion.ExperimentAssertion;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.mem.MemStatsType;
import com.fillumina.performance.mem.stats.MemStatsProducer;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.MemUnit;
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

    private static final Stats MEMSTATS =
            MemStatsProducer.createAllocated()
                .addTest(NOMEMORY, new LfsrRunnable())
                .addTest(NOALLOCATED, () -> Sink.drain(new int[10]) )
                .addTest(ALLOCATED, new Runnable() {
                    final List<Object> list = new ArrayList<>(100);
                    @Override
                    public void run() {
                        list.add(new int[10]);
                    }
                })
                .execute()
                .getStatsHolder(MemStatsType.ALLOCATED)
                .getStats();


    @Test
    public void shouldCheckMultipleAssertion() {
        ExperimentAssertion assertion = Assertions.withTolerance(Ratio.ZERO)
                .assertQuantity(NOMEMORY).equalsTo(MemUnit.B.quantity(0))
                .assertQuantity(NOALLOCATED).equalsTo(MemUnit.B.quantity(0))
                .assertQuantity(ALLOCATED).equalsTo(MemUnit.B.quantity(16 + 4 * 10))
                .assertOrder(NOMEMORY).equalsTo(NOALLOCATED)
                .assertOrder(NOMEMORY).lessThan(ALLOCATED);

        assertion.check(MEMSTATS);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertOrder(NOALLOCATED).equalsTo(ALLOCATED);

        assertion.check(MEMSTATS);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertQuantity(ALLOCATED)
                        .equalsTo(MemUnit.B.quantity(16 + 4 * 10 + 1));

        assertion.check(MEMSTATS);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertQuantity(ALLOCATED)
                        .equalsTo(MemUnit.B.quantity(16 + 4 * 10 + 10));

        assertion.check(MEMSTATS);
    }
}
