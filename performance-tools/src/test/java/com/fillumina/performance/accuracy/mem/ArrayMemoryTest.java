package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.test.SafeSink;
import com.fillumina.performance.template.*;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArrayMemoryTest extends PerformanceTemplate {
    private static final String EMPTY_ARRAY = "empty_array";
    private static final String THOUSAND_ARRAY = "thousand_array";

    public static void main(final String[] args) {
        new ArrayMemoryTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void addAssertions(MixedAssertion<?> assertion) {
        assertion
                .tolerance(Ratio.percentage(0))
                .usedMemory()
                    .assertValue(THOUSAND_ARRAY).sameAs(4 * 1_000 + 16)
                    .assertValue(EMPTY_ARRAY).sameAs(16)
                    .end()
                .allocatedMemory()
                    .assertValue(THOUSAND_ARRAY).sameAs(0)
                    .assertValue(EMPTY_ARRAY).sameAs(0);
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.usedMemConfig();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest(THOUSAND_ARRAY, (Runnable) () -> {
            SafeSink.drain(new int[1_000]);
        });
        tests.addTest(EMPTY_ARRAY, (Runnable) () -> {
            SafeSink.drain(new int[0]);
        });
    }
}
