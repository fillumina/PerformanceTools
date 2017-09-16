package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertionChecker;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.AssertableHolder;
import com.fillumina.performance.infrastructure.test.SafeSink;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemAnalyzerTest {
    private static final String NOMEMORY = "nomemory";
    private static final String ARRAY = "array";

    private static AssertableHolder<MemStats> MEMSTATS;

    @BeforeClass
    public static void initMemStats() {
        MEMSTATS = UsedMemSampleProducer.createMemAnalyzer()
                .addTest(NOMEMORY, (Runnable) () -> { SafeSink.drain(null); })
                .addTest(ARRAY, (Runnable) () -> { SafeSink.drain(new int[10]); })
                .execute()
                .getStats();
    }

    @Test
    public void shouldCheckMultipleAssertion() {
        Assertion assertion =
                AssertionChecker.withTolerance(Ratio.percentage(10))
                .assertValue(NOMEMORY).sameAs(0)
                .assertValue(ARRAY).sameAs(16 + 4 * 10)
                .assertOrder(NOMEMORY).lessThan(ARRAY);

        MEMSTATS.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        Assertion assertion =
                AssertionChecker.withTolerance(Ratio.percentage(10))
                .assertOrder(NOMEMORY).sameAs(ARRAY);

        MEMSTATS.check(assertion);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        Assertion assertion =
                AssertionChecker.withTolerance(Ratio.percentage(10))
                .assertValue(ARRAY).sameAs(16 + 4 * 10 + 1);

        MEMSTATS.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        Assertion assertion =
                AssertionChecker.withTolerance(Ratio.percentage(10))
                .assertValue(ARRAY).sameAs(16 + 4 * 10 + 10);

        MEMSTATS.check(assertion);
    }
}
