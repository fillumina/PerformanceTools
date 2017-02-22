package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.testable.LfsrTestable;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceTemplateAsFluidTest {
    private static final String NO_MEMORY = "noMemory";
    private static final String MEMORY_HOG = "memoryHog";

    public static void main(final String[] args) {
        new AutoProgressionPerformanceTemplateAsFluidTest()
                .createTemplate().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        createTemplate().executeWithoutOutput();
    }

    private PerformanceTemplate createTemplate() {
        return new PerformanceTemplate() {

            @Override
            public void addAssertions(ProgressionAssertion assertion) {
                assertion.speedWithTolerance(10)
                        .assertOrder(NO_MEMORY).lessThan(MEMORY_HOG);

                assertion.usedMemoryWithTolerance(5)
                        .assertValue(MEMORY_HOG).sameAs(4016)
                        .assertValue(NO_MEMORY).sameAs(16);

                assertion.allocatedMemoryWithTolerance(5)
                        .assertValue(MEMORY_HOG).sameAs(0)
                        .assertValue(NO_MEMORY).sameAs(0);
            }

            @Override
            public void config(TestConfiguration configuration) {
                configuration
                        .speedTest()
                            .setTimeoutSeconds(120)
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
                tests.addTest(NO_MEMORY, new LfsrTestable());
            }

        };
    }
}
