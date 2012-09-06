package com.fillumina.performance.examples;

import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.junit.JUnitPerformanceTestHelper;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class InheritanceAgainstCompositionPerformanceExampleTest
        extends JUnitPerformanceTestHelper {

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
        new InheritanceAgainstCompositionPerformanceExampleTest()
                .testWithOutput();
    }

    @Override
    public void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("inheritance", new Runnable() {

            @Override
            public void run() {
                assertEquals(12, new ExtendingMultiplier().doOperation(4, 3));
            }
        });

        pt.addTest("composition", new Runnable() {

            @Override
            public void run() {
                assertEquals(12, new ComposedClass().doOperation(4, 3));
            }
        });

        pt.addPerformanceConsumer(iterationConsumer);

        // composition wins by being faster by 67% faster than inheritance
        pt.instrumentedBy(AutoProgressionPerformanceInstrumenter.builder())
                .setTimeout(10, TimeUnit.MINUTES)
                .setMaxStandardDeviation(0.6)
                .setSamplePerMagnitude(10)
                .build()
                .addPerformanceConsumer(resultConsumer)
                .addPerformanceConsumer(AssertPerformance.withTolerance(5F)
                        .assertTest("inheritance").slowerThan("composition"))
                .execute();
    }

}
