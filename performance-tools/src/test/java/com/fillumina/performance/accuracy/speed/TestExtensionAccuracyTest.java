package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.infrastructure.DoubleLfsrTestable;
import com.fillumina.performance.infrastructure.LfsrTestable;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestExtensionAccuracyTest {

    public static void main(final String[] args) {
        new TestExtensionAccuracyTest()
                .shouldDifferentObjectsOfSameClassBeAccurate();
    }

    private static class Shared extends Testable {
        private final Testable testable;

        public Shared(Testable testable) {
            this.testable = testable;
        }

        @Override
        public void run() {
            testable.run();
        }
    }

    @Test
    public void shouldDifferentObjectsOfSameClassBeAccurate() {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(ProgressionAssertion assertions) {
                assertions.speedWithTolerance(Ratio.percentage(5))
                        .assertPercentage("single").sameAs(50);
            }

            @Override
            public void config(TestConfiguration config) {
                config.speedTestOnly();
            }

            @Override
            public void addTests(TestContainer<Runnable> tests) {
                tests.addTest("double", new Shared(new DoubleLfsrTestable()));
                tests.addTest("single", new Shared(new LfsrTestable()));
            }

        }.executeWithFullOutput();
    }
}
