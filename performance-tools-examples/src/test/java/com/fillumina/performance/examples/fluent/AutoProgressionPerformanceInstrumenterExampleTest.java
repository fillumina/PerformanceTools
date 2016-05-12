package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.viewer.StringCsvSampleViewer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Shows both ways to define an auto progression performance test:
 * <ul>
 * <li>By defining the
 *      {@link com.fillumina.performance.sample.DefaultPerformanceTimer}
 *      first and than instrument it
 *      with the {@link AutoProgressionPerformanceInstrumenter}.</li>
 * <li>By defining the {@link AutoProgressionPerformanceInstrumenter} first
 *      and than set a
 *      {@link com.fillumina.performance.sample.DefaultPerformanceTimer}
 *      to it.</li>
 * </ul>
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionPerformanceInstrumenterExampleTest {
    private final static int[] ARRAY =
            new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private static final String BOUNDARY = "boundary";
    private static final String EXCEPTION = "exception";

    public static void main(final String[] args) {
        final AutoProgressionPerformanceInstrumenterExampleTest test =
                new AutoProgressionPerformanceInstrumenterExampleTest();

        test.testInstrumentedBy(StringCsvSampleViewer.INSTANCE,
                StringTableStatsViewer.INSTANCE);

        test.testInstrument(StringCsvSampleViewer.INSTANCE,
                StringTableStatsViewer.INSTANCE);
    }

    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentTest() {
        testInstrument(NullPerformanceConsumer.<PerformanceSample>instance(),
                NullPerformanceConsumer.<PerformanceStats>instance());
    }

    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentedByTest() {
        testInstrumentedBy(NullPerformanceConsumer.<PerformanceSample>instance(),
                NullPerformanceConsumer.<PerformanceStats>instance());
    }


    private final AbstractTestable EXCEPTION_TEST = new AbstractTestable() {
        private int counter = 10;

        @Override
        public Object test() {
            try {
                ARRAY[counter] = counter;
                counter++;
            } catch (ArrayIndexOutOfBoundsException e) {
                counter = 0;
            }
            return counter;
        }
    };

    private final AbstractTestable BOUNDARY_TEST = new AbstractTestable() {
        private int counter = 0;

        @Override
        public Object test() {
            if (counter < ARRAY.length) {
                ARRAY[counter] = counter;
                counter++;
            } else {
                counter = 0;
            }
            return counter;
        }
    };

    /** First defines the DefaultPerformanceTimer than instrument it. */
    private void testInstrumentedBy(
            final PerformanceConsumer<PerformanceSample> iterationConsumer,
            final PerformanceConsumer<PerformanceStats> resultConsumer) {
        PerformanceTimerFactory
            .createSingleThreaded()

            .addTest(BOUNDARY, BOUNDARY_TEST)
            .addTest(EXCEPTION, EXCEPTION_TEST)

            .addPerformanceConsumer(iterationConsumer)

            .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setName("InstrumentedBy")
                    .setTimeout(1000, TimeUnit.SECONDS) // increase to ease debugging
                    .setAddBaselineTest(false)
                    .build())
                .addPerformanceConsumer(resultConsumer)
                .execute()
                .use(AssertPerformance.withTolerance(5F)
                    .assertSpeed(BOUNDARY).slowerThan(EXCEPTION));
    }

    /** First defines the instrumenter than set a DefaultPerformanceTimer to it. */
    private void testInstrument(
            final PerformanceConsumer<PerformanceSample> iterationConsumer,
            final PerformanceConsumer<PerformanceStats> resultConsumer) {

        AutoProgressionPerformanceInstrumenter.builder()
                .setName("Instrument")
                .setTimeout(1000, TimeUnit.SECONDS) // to ease debugging
                .setAddBaselineTest(false)
                .build()
                .instrument(PerformanceTimerFactory
                    .createSingleThreaded()
                    .addTest(BOUNDARY, BOUNDARY_TEST)
                    .addTest(EXCEPTION, EXCEPTION_TEST)
                    .addPerformanceConsumer(iterationConsumer))

                .addPerformanceConsumer(resultConsumer)
                .execute()
                .use(AssertPerformance.withTolerance(5F)
                    .assertSpeed(BOUNDARY).slowerThan(EXCEPTION));

    }
}
