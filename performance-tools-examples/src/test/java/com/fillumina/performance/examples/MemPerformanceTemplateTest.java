package com.fillumina.performance.examples;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.MemUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemPerformanceTemplateTest extends PerformanceTemplate {
    private static final String NO_MEMORY = "noMemory";
    private static final String MEMORY_HOG = "memoryHog";

    public static void main(final String[] args) {
        new MemPerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config
            .speedConfig().end()
            .usedMemConfig().end()
            .allocatedMemConfig().end();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests
            .addTest(MEMORY_HOG, () -> Sink.drain(new int[1_000]) )
            .addTest(NO_MEMORY, new LfsrRunnable());
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertion) {
        assertion
                .tolerance(Ratio.percentage(0))
                .avgTime()
                    .order(NO_MEMORY).lessThan(MEMORY_HOG)
                    .end()
                .usedMemory()
                    .value(MEMORY_HOG).equalsTo(MemUnit.B.quantity(4_000 + 16))
                    .value(NO_MEMORY).equalsTo(MemUnit.B.zero())
                    .end()
                .allocatedMemory()
                    .value(MEMORY_HOG).equalsTo(MemUnit.B.zero())
                    .value(NO_MEMORY).equalsTo(MemUnit.B.zero());
    }
}
