package com.fillumina.performance.examples;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;

/**
 * This approach is a long standing argument about composition against
 * extending a class.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class InheritanceAgainstCompositionApp
        extends PerformanceTemplate {

    public static void main(final String[] args) {
        new InheritanceAgainstCompositionApp()
                .executeWithFullOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedConfig();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("composition", new Runnable() {
            private final ComposedClass cc = new ComposedClass();
            private int a = 4, b = 7889;

            @Override
            public void run() {
                Sink.drain(cc.doOperation(a++, b++));
            }
        });

        tests.addTest("inheritance", new Runnable() {
            private final ExtendingMultiplier em = new ExtendingMultiplier();
            private int a = 4, b = 7889;

            @Override
            public void run() {
                Sink.drain(em.doOperation(a++, b++));
            }
        });
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions.avgTime().order("composition").equalsTo("inheritance").end();
    }

    private static abstract class AbstractInheritableClass {

        public abstract int multiply(int a, int b);

        public int doOperation(int a, int b) {
            return multiply(a, b);
        }
    }

    private static class ExtendingMultiplier extends AbstractInheritableClass {

        @Override
        public int multiply(int a, int b) {
            return a * b;
        }
    }

    private static class StandAloneMultiplier {

        public int multiply(int a, int b) {
            return a * b;
        }
    }

    private static class ComposedClass {
        private final static StandAloneMultiplier MULTIPLIER =
                new StandAloneMultiplier();

        public int doOperation(int a, int b) {
            return MULTIPLIER.multiply(a, b);
        }
    }
}