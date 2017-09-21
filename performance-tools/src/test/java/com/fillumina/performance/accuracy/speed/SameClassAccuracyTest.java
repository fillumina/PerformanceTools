package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.executor.test.DoubleLfsrRunnable;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.time.stats.AverageTimeStats;
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
    public void addAssertions(MixedAssertion<?> assertions) {
        assertions.avgTime()
                .assertPercentage("single").sameAs(50);

        assertions.addAssertion(AverageTimeStats.class)
                .assertPercentage("single").sameAs(50);
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedConfig().setConfidence(Ratio.P_99);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("double", new Shared(new DoubleLfsrRunnable()));
        tests.addTest("single", new Shared(new LfsrRunnable()));
    }
}
