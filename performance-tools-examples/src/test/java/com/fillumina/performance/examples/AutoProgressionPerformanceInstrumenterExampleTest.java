package com.fillumina.performance.examples;

import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Shows both ways of define an auto progression performance test:
 * <ul>
 * <li>By defining the
 *      {@link com.fillumina.performance.producer.timer.PerformanceTimer}
 *      first and than instrument it
 *      with the {@link AutoProgressionPerformanceInstrumenter}.</li>
 * <li>By defining the {@link AutoProgressionPerformanceInstrumenter} first
 *      and than set a
 *      {@link com.fillumina.performance.producer.timer.PerformanceTimer}
 *      to it.</li>
 * </ul>
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceInstrumenterExampleTest {
    private final static int MAX = 10;
    private final static int[] REFERENCE =
            new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private static final String BOUNDARY_CHECK = "boundary check";
    private static final String EXCEPTION = "exception";

    public static void main(final String[] args) {
        final AutoProgressionPerformanceInstrumenterExampleTest test =
                new AutoProgressionPerformanceInstrumenterExampleTest();
        test.testInstrumentedBy(StringCsvViewer.INSTANCE, StringTableViewer.INSTANCE);
        test.testInstrument(StringCsvViewer.INSTANCE, StringTableViewer.INSTANCE);
    }

    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentTest() {
        testInstrument(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentedByTest() {
        testInstrumentedBy(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    /** First defines the PerformanceTimer than instrument it. */
    private void testInstrumentedBy(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {
        PerformanceTimerFactory
            .createSingleThreaded()

            .addTest(BOUNDARY_CHECK, new Runnable() {
                private int counter = 0;
                private int[] array = new int[MAX];

                @Override
                public void run() {
                    if (counter < MAX) {
                        array[counter] = counter;
                        counter++;
                    } else {
                        assertArrayEquals(REFERENCE, array);
                        counter = 0;
                    }
                }
            })

            .addTest(EXCEPTION, new Runnable() {
                private int counter = 0;
                private int[] array = new int[MAX];

                @Override
                public void run() {
                    try {
                        array[counter] = counter;
                        counter++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        assertArrayEquals(REFERENCE, array);
                        counter = 0;
                    }
                }
            })

            .addPerformanceConsumer(iterationConsumer)

            .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setTimeout(10, TimeUnit.SECONDS) // to ease debugging
                        .setSamplesPerStep(MAX + 3)
                        .setMaxStandardDeviation(1.4)
                        .build())
                    .addPerformanceConsumer(resultConsumer)
                    .execute()
                    .use(AssertPerformance.withTolerance(5F)
                        .assertTest(BOUNDARY_CHECK).sameAs(EXCEPTION));
    }

    /** First defines the instrumenter than set a PerformanceTimer to it. */
    private void testInstrument(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        AutoProgressionPerformanceInstrumenter.builder()
                .setTimeout(10, TimeUnit.SECONDS) // to ease debugging
                .setSamplesPerStep(MAX + 3)
                .setMaxStandardDeviation(1.4)
                .build()
                .instrument(PerformanceTimerFactory
                    .createSingleThreaded()

                    .addTest(BOUNDARY_CHECK, new Runnable() {
                        private int counter = 0;
                        private int[] array = new int[MAX];

                        @Override
                        public void run() {
                            if (counter < MAX) {
                                array[counter] = counter;
                                counter++;
                            } else {
                                assertArrayEquals(REFERENCE, array);
                                counter = 0;
                            }
                        }
                    })

                    .addTest(EXCEPTION, new Runnable() {
                        private int counter = 0;
                        private int[] array = new int[MAX];

                        @Override
                        public void run() {
                            try {
                                array[counter] = counter;
                                counter++;
                            } catch (ArrayIndexOutOfBoundsException e) {
                                assertArrayEquals(REFERENCE, array);
                                counter = 0;
                            }
                        }
                    }).addPerformanceConsumer(iterationConsumer))

                .addPerformanceConsumer(resultConsumer)
                .execute()
                .use(AssertPerformance.withTolerance(5F)
                    .assertTest(BOUNDARY_CHECK).sameAs(EXCEPTION));

    }
}
