package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.producer.timer.FakePerformanceTimer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.LoopPerformancesCreator;
import com.fillumina.performance.util.CountingMap;
import com.fillumina.performance.util.NullRunnableObject;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * It's a way to check if the auto progression algorithm converges.
 * The test has been made artificially converging after the given number
 * of iterations.
 *
 * @author fra
 */
public class AutoProgressionPerformanceInstrumenterTest {
    public static final int SAMPLES_PER_ITERATION = 10;

    public static void main(final String[] args) {
        new AutoProgressionPerformanceInstrumenterTest()
                .iterate(StringCsvViewer.CONSUMER);
    }

    @Test
    public void shouldProgressOverTwoSetOfIterations() {
        iterate(NullPerformanceConsumer.INSTANCE);
    }

    private void iterate(final PerformanceConsumer consumer) {
        final CountingMap<Integer> countingMap = new CountingMap<>();

        FakePerformanceTimer fpt = new FakePerformanceTimer() {
            private static final long serialVersionUID = 1L;
            private final Random rnd = new Random();

            @Override
            public LoopPerformances getLoopPerformances(final int iterations) {
                countingMap.increment(iterations);
                if (iterations < 1_000) {
                    return createHighVarianceLoopPerformances(iterations);
                }
                return createStableLoopPerformances(iterations);
            }

            private LoopPerformances createHighVarianceLoopPerformances(
                    final int iterations) {
                return LoopPerformancesCreator.parse(iterations, new Object[][] {
                    {"first", rnd.nextInt(10)},
                    {"second", rnd.nextInt(20)},
                    {"full", 100}
                });
            }

            private LoopPerformances createStableLoopPerformances(
                    final int iterations) {
                return LoopPerformancesCreator.parse(iterations, new Object[][] {
                    {"first", 10},
                    {"second", 20},
                    {"full", 100}
                });
            }

        };


        fpt.addTest("first", new NullRunnableObject());
        fpt.addTest("second", new NullRunnableObject());

        fpt.addPerformanceConsumer(consumer);

        fpt.instrumentedBy(AutoProgressionPerformanceInstrumenter.builder())
            .setTimeout(1, TimeUnit.DAYS) // to allow an easy debugging
            .setSamplePerIterations(SAMPLES_PER_ITERATION)
            .setMaxStandardDeviation(0.4)
            .build()
            .executeSequence();

        // while the performances have a variance greater than 0.4 it keeps incrementing
        assertEquals(SAMPLES_PER_ITERATION, countingMap.getCounterFor(10));
        assertEquals(SAMPLES_PER_ITERATION, countingMap.getCounterFor(100));
        assertEquals(SAMPLES_PER_ITERATION, countingMap.getCounterFor(1_000));

        // it stops at 1_000 iterations when the variance becomes 0
        assertEquals(0, countingMap.getCounterFor(10_000));
    }
}
