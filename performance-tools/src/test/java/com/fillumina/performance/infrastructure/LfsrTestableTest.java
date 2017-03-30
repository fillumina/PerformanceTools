package com.fillumina.performance.infrastructure;

import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrTestableTest extends PerformanceTemplate {

    public static void main(final String[] args) {
        new LfsrTestableTest().executeWithFullOutput();
    }

    @Override
    public void addAssertions(ProgressionAssertion assertions) {
        assertions.speedWithTolerance(Ratio.percentage(5))
                .assertOrder("lfsr").lessThan("counter");

        assertions.allocatedMemoryWithTolerance(Ratio.ZERO)
                .assertValue("lfsr").sameAs(0);

        assertions.usedMemoryWithTolerance(Ratio.ZERO)
                .assertValue("lfsr").sameAs(0);
    }

    @Override
    public void config(TestConfiguration config) {
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("lfsr", new LfsrTestable());
        tests.addTest("counter", new Testable() {
            private volatile int counter;
            @Override
            public void test() {
                drain(counter++);
            }
        });
    }
}
