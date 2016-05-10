package com.fillumina.performance.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class BaseMagnitudeProgressionPerformanceInstrumenterTest {

    @Test
    public void shouldRunTheDeclaredIterationsDefinedUsingBaseAndMagnitude() {
        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(10)
                .setMagnitude(3)
                .setSamples(10)
                .assertSamples(10, 100, 1000);

        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(5)
                .setMagnitude(4)
                .setSamples(2)
                .assertSamples(5, 50, 500, 5000);

        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(1)
                .setMagnitude(4)
                .setSamples(1)
                .assertSamples(1, 10, 100, 1000);

    }

    private static class BaseMagnitudeProgressionChecker {
        private int baseTimes;
        private int magnitude;
        private int samples;

        public BaseMagnitudeProgressionChecker setBaseTimes(int baseTimes) {
            this.baseTimes = baseTimes;
            return this;
        }

        public BaseMagnitudeProgressionChecker setMagnitude(int magnitude) {
            this.magnitude = magnitude;
            return this;
        }

        public BaseMagnitudeProgressionChecker setSamples(int samples) {
            this.samples = samples;
            return this;
        }

        private void assertSamples(final int... iterations) {
            final AssertIterationsPerformanceConsumer assertIterations =
                    new AssertIterationsPerformanceConsumer()
                        .setIterations(iterations)
                        .setSamplesPerIteration(samples);

            PerformanceTimerFactory.createSingleThreaded()

            .addTest("counter", new AbstractTestable() {

                @Override
                public Object test() {
                    return null;
                }
            })

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setBaseAndMagnitude(baseTimes, magnitude)
                    .setEliminateOutliers(false)
                    .setAddBaselineTest(false)
                    .setSamplesPerStep(samples)
                    .build())

            .addPerformanceConsumer(assertIterations)

            .execute();

            assertIterations.assertIterationsNumber(iterations.length);
        }
    }
}
