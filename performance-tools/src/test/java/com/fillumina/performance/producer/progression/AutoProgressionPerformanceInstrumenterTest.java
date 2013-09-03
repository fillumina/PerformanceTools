package com.fillumina.performance.producer.progression;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.producer.timer.FakePerformanceTimer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesCreator;
import com.fillumina.performance.util.Bag;
import com.fillumina.performance.util.EmptyRunnable;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * It's a way to validate if the auto progression algorithm converges.
 * The test has been made artificially converging after the given number
 * of iterations.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceInstrumenterTest {
    public static final int ITERATIONS = 10;

    public static void main(final String[] args) {
        new AutoProgressionPerformanceInstrumenterTest()
                .iterate(StringCsvViewer.CONSUMER);
    }

    @Test
    public void shouldProgressOverTwoSetOfIterations() {
        iterate(NullPerformanceConsumer.INSTANCE);
    }

    private void iterate(final PerformanceConsumer consumer) {
        final Bag<Long> countingMap = new Bag<>();

        FakePerformanceTimer fpt = new FakePerformanceTimer() {
            private static final long serialVersionUID = 1L;
            private final Random rnd = new Random();

            @Override
            public LoopPerformances getLoopPerformances(final long iterations) {
                countingMap.add(iterations);
                if (iterations < 1_000) {
                    return createHighVarianceLoopPerformances(iterations);
                }
                return createStableLoopPerformances(iterations);
            }

            private LoopPerformances createHighVarianceLoopPerformances(
                    final long iterations) {
                return LoopPerformancesCreator.parse(iterations, new Object[][] {
                    {"first", rnd.nextInt(10)},
                    {"second", rnd.nextInt(20)},
                    {"full", 100}
                });
            }

            private LoopPerformances createStableLoopPerformances(
                    final long iterations) {
                return LoopPerformancesCreator.parse(iterations, new Object[][] {
                    {"first", 10},
                    {"second", 20},
                    {"full", 100}
                });
            }
        };

        fpt.addTest("first", EmptyRunnable.INSTANCE);
        fpt.addTest("second", EmptyRunnable.INSTANCE);

        fpt.addPerformanceConsumer(consumer);

        final AutoProgressionPerformanceInstrumenter instrumenter =
                AutoProgressionPerformanceInstrumenter.builder()
                    .setTimeout(1, TimeUnit.DAYS) // to allow an easy debugging
                    .setSamplesPerMagnitude(ITERATIONS)
                    .setBaseIterations(10)
                    .setMaxStandardDeviation(0.4)
                    .setCheckStdDeviation(false)
                    .build();

        fpt.instrumentedBy(instrumenter)
            .execute();

        // while the performances have a variance greater than 0.4 it keeps incrementing
        assertEquals(ITERATIONS, countingMap.getCount(10L));
        assertEquals(ITERATIONS, countingMap.getCount(100L));
        assertEquals(ITERATIONS, countingMap.getCount(1_000L));

        // it stops at 10_000 iterations when the variance becomes 0
        assertEquals(0, countingMap.getCount(10_000L));
    }
}
