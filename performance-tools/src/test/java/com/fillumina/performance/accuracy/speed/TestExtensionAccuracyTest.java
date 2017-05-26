package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.infrastructure.DoubleLfsrRunnable;
import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.template.Configuration;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
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

    private static class Shared implements Runnable {
        private final Runnable testable;

        public Shared(Runnable testable) {
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
            public void addAssertions(MixedAssertion assertions) {
                assertions.speed()
                        .assertPercentage("single").sameAs(50);
            }

            @Override
            public void config(Configuration config) {
                config.speedTestOnly();
            }

            @Override
            public void addTests(TestConfiguration<?> tests) {
                tests.addTest("double", new Shared(new DoubleLfsrRunnable()));
                tests.addTest("single", new Shared(new LfsrRunnable()));
            }

        }.executeWithFullOutput();
    }
}
