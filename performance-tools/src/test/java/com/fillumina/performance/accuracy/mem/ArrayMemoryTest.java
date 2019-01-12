package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.*;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.MemUnit;
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
    public void config(MixedConfigurationBuilder<?> config) {
        config.usedMemConfig();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest(THOUSAND_ARRAY, (Runnable) () -> {
            Sink.drain(new int[1_000]);
        });
        tests.addTest(EMPTY_ARRAY, (Runnable) () -> {
            Sink.drain(new int[0]);
        });
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertion) {
        assertion
                .tolerance(Ratio.percentage(0))
                .usedMemory()
                    .value(THOUSAND_ARRAY)
                    .equalsTo(MemUnit.B.quantity(4 * 1_000 + 16))
                    .value(EMPTY_ARRAY)
                    .equalsTo(MemUnit.B.quantity(16))
                    .end()
                .allocatedMemory()
                    .value(THOUSAND_ARRAY).equalsTo(MemUnit.B.zero())
                    .value(EMPTY_ARRAY).equalsTo(MemUnit.B.zero());
    }
}
