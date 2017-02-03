package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.LfsrTest;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceTemplateTest
        extends AutoProgressionPerformanceTemplate {
    private static final String NO_MEMORY = "noMemory";
    private static final String MEMORY_HOG = "memoryHog";

    public static void main(final String[] args) {
        new AutoProgressionPerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.speedWithTolerance(5)
                    .assertOrder(NO_MEMORY).lessThan(MEMORY_HOG)
                    .end()
                .usedMemoryWithTolerance(5)
                    .assertValue(MEMORY_HOG).sameAs(4016)
                    .assertValue(NO_MEMORY).sameAs(16)
                    .end()
                .allocatedMemoryWithTolerance(5)
                    .assertValue(MEMORY_HOG).sameAs(0)
                    .assertValue(NO_MEMORY).sameAs(0);
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration
                .speedTest()
                    .setTimeoutSeconds(60)
                .usedMemTest()
                .allocatedMemTest();
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest(MEMORY_HOG, new AbstractTestable() {
            @Override
            public Object test() {
                return new int[1_000];
            }
        });
        tests.addTest(NO_MEMORY, LfsrTest.INSTANCE);
    }
}
