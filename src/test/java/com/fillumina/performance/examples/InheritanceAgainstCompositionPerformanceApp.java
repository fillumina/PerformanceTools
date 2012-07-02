package com.fillumina.performance.examples;

import com.fillumina.performance.timer.PerformanceTimer;
import com.fillumina.performance.timer.PerformanceTimerBuilder;
import com.fillumina.performance.sequence.ProgressionSequence;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class InheritanceAgainstCompositionPerformanceApp {

    private static abstract class AbstractInheritingClass {

        public abstract int multiply(int a, int b);

        public int doOperation(int a, int b) {
            return multiply(a, b);
        }
    }

    private static class ExtendingMultiplier extends AbstractInheritingClass {

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
        private final static StandAloneMultiplier multiplier = new StandAloneMultiplier();

        public int doOperation(int a, int b) {
            return multiplier.multiply(a, b);
        }
    }


    public static void main(final String[] args) {
        new InheritanceAgainstCompositionPerformanceApp().test();
    }

    public void test() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("inheritance", new Runnable() {
            private ExtendingMultiplier em = new ExtendingMultiplier();

            @Override
            public void run() {
                assertEquals(12, em.doOperation(4, 3));
            }
        });

        pt.addTest("composition", new Runnable() {
            private ComposedClass cc = new ComposedClass();

            @Override
            public void run() {
                assertEquals(12, cc.doOperation(4, 3));
            }
        });

        // composition wins by being faster by 67% faster than inheritance
        new ProgressionSequence(pt).serie(10_000, 6, 10);
    }

}
