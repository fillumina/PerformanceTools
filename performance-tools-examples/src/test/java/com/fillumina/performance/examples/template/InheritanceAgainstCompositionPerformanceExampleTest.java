package com.fillumina.performance.examples.template;

import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.examples.template.InheritanceAgainstCompositionPerformanceExampleTest.Multiplier;
import com.fillumina.performance.producer.suite.ParametersContainer;
import com.fillumina.performance.producer.suite.ParametrizedExecutor;
import com.fillumina.performance.producer.suite.ParametrizedRunnable;
import com.fillumina.performance.template.ProgressionConfigurator;
import com.fillumina.performance.util.junit.JUnitParametrizedPerformanceTemplate;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

/**
 * Uses the junit template to define a multi object test.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class InheritanceAgainstCompositionPerformanceExampleTest
        extends JUnitParametrizedPerformanceTemplate<Multiplier> {

    static interface Multiplier {
        int multiply(int a, int b);
    }

    private static abstract class AbstractInheritingClass implements Multiplier {

        public abstract int operate(int a, int b);

        @Override
        public int multiply(int a, int b) {
            return operate(a, b);
        }
    }

    /** Using a virtual method call in inheritance. */
    private static class ExtendingMultiplier extends AbstractInheritingClass {

        @Override
        public int operate(int a, int b) {
            return a * b;
        }
    }

    /** Using a direct in class method call. */
    private static class StandAloneMultiplier implements Multiplier {

        public int operate(int a, int b) {
            return a * b;
        }

        @Override
        public int multiply(int a, int b) {
            return operate(a, b);
        }
    }

    /** Using a composed class. */
    private static class ComposedMultiplier implements Multiplier {
        private final static StandAloneMultiplier multiplier =
                new StandAloneMultiplier();

        @Override
        public int multiply(int a, int b) {
            return multiplier.operate(a, b);
        }
    }

    public static void main(final String[] args) {
        new InheritanceAgainstCompositionPerformanceExampleTest()
                .testWithDetailedOutput();
    }

    @Override
    public void init(final ProgressionConfigurator config) {
        config.setTimeout(10, TimeUnit.MINUTES) // to ease debugging
            .setMaxStandardDeviation(1)
            .setSamplesPerStep(10);

    }

    @Override
    public void addParameters(
            final ParametersContainer<Multiplier> parameters) {
        parameters
            .addParameter("direct", new StandAloneMultiplier())
            .addParameter("inheritance", new ExtendingMultiplier())
            .addParameter("composition", new ComposedMultiplier());
    }

    @Override
    public void addAssertions(final SuiteExecutionAssertion assertion) {
        assertion.forDefaultExecution()
                .assertTest("inheritance").sameAs("composition")
                .assertTest("direct").sameAs("composition");
    }

    @Override
    public void executeTests(
            final ParametrizedExecutor<Multiplier> executor) {
        executor.executeTest(new ParametrizedRunnable<Multiplier>() {
            private final Random rnd = new Random();

            @Override
            public void call(final Multiplier multiplier) {
                final int a = rnd.nextInt(10_000);
                final int b = rnd.nextInt(10_000);
                assertEquals(a * b, multiplier.multiply(a, b));
            }
        });
    }
}
