package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.LsfrTestable;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ShouldNoConfigMeansAllTest
        extends PerformanceTemplate {
    private static final String TEST = "test";

    public static void main(final String[] args) {
        new ShouldNoConfigMeansAllTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new ShouldNoConfigMeansAllTest().executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        // left empty
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest(TEST, new LsfrTestable());
    }

    @Override
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.speedWithTolerance(5)
                .assertPercentage(TEST).sameAs(100);
        assertion.usedMemoryWithTolerance(5)
                .assertValue(TEST).sameAs(16);
        assertion.allocatedMemoryWithTolerance(5)
                .assertValue(TEST).sameAs(0);
    }
}
