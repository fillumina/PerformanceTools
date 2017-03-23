package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mock.LfsrTestable;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.Testable;
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
    public void config(TestConfiguration configuration) {
        configuration
                .speedTest()
                .usedMemTest()
                .allocatedMemTest();
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest(MEMORY_HOG, new Testable() {
            @Override
            public void test() {
                Sink.drain(new int[1_000]);
            }
        });
        tests.addTest(NO_MEMORY, new LfsrTestable());
    }
}
