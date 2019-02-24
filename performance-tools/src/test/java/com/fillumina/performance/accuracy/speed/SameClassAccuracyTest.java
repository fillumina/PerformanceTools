package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.DoubleLfsrRunnable;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SameClassAccuracyTest extends PerformanceTemplate {

    public static void main(final String[] args) {
        new SameClassAccuracyTest().executeWithFullOutput();
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
    public void shouldEvaluateTwoDifferentTestWithTheSameClass() {
        executeWithoutOutput();
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions.avgTime().percentage("single").equalsTo(Ratio.P_50);
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedConfig()
                .setSamples(10)
                .setConfidence(Ratio.P_99);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("double", new Shared(new DoubleLfsrRunnable()));
        tests.addTest("double2", new Shared(new DoubleLfsrRunnable()));
        tests.addTest("single", new Shared(new LfsrRunnable()));
        tests.addTest("single2", new Shared(new LfsrRunnable()));
    }
}
