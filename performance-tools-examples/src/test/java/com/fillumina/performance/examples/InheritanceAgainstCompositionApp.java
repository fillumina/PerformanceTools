package com.fillumina.performance.examples;

import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;

/**
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
        config.speedTestOnly();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("composition", new Runnable() {
            private int a = 4, b = 7889;
            private ComposedClass cc = new ComposedClass();

            @Override
            public void run() {
                Sink.drain(cc.doOperation(a++, b++));
            }
        });

        tests.addTest("inheritance", new Runnable() {
            private int a = 4, b = 7889;
            private ExtendingMultiplier em = new ExtendingMultiplier();

            @Override
            public void run() {
                Sink.drain(em.doOperation(a++, b++));
            }
        });
    }

    @Override
    public void addAssertions(MixedAssertion<?> assertions) {
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
        private final static StandAloneMultiplier multiplier =
                new StandAloneMultiplier();

        public int doOperation(int a, int b) {
            return multiplier.multiply(a, b);
        }
    }
}