package com.fillumina.performance.examples;

import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.junit.JUnitSimplePerformanceTemplate;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class InheritanceAgainstCompositionPerformanceExampleTest
        extends JUnitSimplePerformanceTemplate {

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

    private static abstract class RandomMultiplication implements Runnable {
        private Random rnd = new Random();
        protected abstract int calculate(int a, int b);

        @Override
        public void run() {
            final int a = rnd.nextInt(10_000);
            final int b = rnd.nextInt(10_000);
            assertEquals(a * b, calculate(a, b));
        }
    }

    public static void main(final String[] args) {
        new InheritanceAgainstCompositionPerformanceExampleTest()
                .testWithDetailedOutput();
    }

    @Override
    public void executePerformanceTest(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        final PerformanceTimer pt = PerformanceTimerFactory.createSingleThreaded();

        pt.addTest("inheritance", new RandomMultiplication() {

            @Override
            protected int calculate(int a, int b) {
                return new ExtendingMultiplier().doOperation(a, b);
            }
        });

        pt.addTest("composition", new RandomMultiplication() {

            @Override
            protected int calculate(int a, int b) {
                return new ComposedClass().doOperation(a, b);
            }
        });

        pt.addPerformanceConsumer(iterationConsumer);

        pt.instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setTimeout(10, TimeUnit.MINUTES) // to ease debugging
                    .setMaxStandardDeviation(1)
                    .setSamplesPerMagnitude(10)
                    .build())
                .addPerformanceConsumer(resultConsumer)
                .addPerformanceConsumer(AssertPerformance.withTolerance(5F)
                        .assertTest("inheritance").slowerThan("composition"))
                .execute();
    }

}
