package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTemplateTest
        extends PerformanceTemplate {
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
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.speedWithTolerance(Ratio.percentage(10))
                    .assertOrder(NO_MEMORY).lessThan(MEMORY_HOG)
                    .end()
                .usedMemoryWithTolerance(Ratio.percentage(5))
                    .assertValue(MEMORY_HOG).sameAs(4016)
                    .assertValue(NO_MEMORY).sameAs(0)
                    .end()
                .allocatedMemoryWithTolerance(Ratio.percentage(5))
                    .assertValue(MEMORY_HOG).sameAs(0)
                    .assertValue(NO_MEMORY).sameAs(0);
    }

    @Override
    public void config(Configuration<PerformanceTemplate> config) {
        config
            .speedTest().end()
            .usedMemTest().end()
            .allocatedMemTest();
    }

    @Override
    public void addTests(TestContainer<Runnable> tests) {
        tests.addTest(MEMORY_HOG, new Runnable() {
            @Override
            public void run() {
                Sink.drain(new int[1_000]);
            }
        });
        tests.addTest(NO_MEMORY, new LfsrRunnable());
    }
}
