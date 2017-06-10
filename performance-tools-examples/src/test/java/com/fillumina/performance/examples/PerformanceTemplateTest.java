package com.fillumina.performance.examples;

import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTemplateTest extends PerformanceTemplate {
    private static final String NO_MEMORY = "noMemory";
    private static final String MEMORY_HOG = "memoryHog";

    public static void main(final String[] args) {
        new PerformanceTemplateTest()
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
                .speed()
                    .assertOrder(NO_MEMORY).lessThan(MEMORY_HOG)
                    .end()
                .usedMemory()
                    .assertValue(MEMORY_HOG).sameAs(4_000 + 16)
                    .assertValue(NO_MEMORY).sameAs(0)
                    .end()
                .allocatedMemory()
                    .assertValue(MEMORY_HOG).sameAs(0)
                    .assertValue(NO_MEMORY).sameAs(0);
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config
            .speed().end()
            .usedMem().end()
            .allocatedMem().end();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest(MEMORY_HOG, (Runnable) () -> {
            Sink.drain(new int[1_000]);
        });
        tests.addTest(NO_MEMORY, new LfsrRunnable());
    }
}
