package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.PerformanceSample;
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
    private static final String BOUNDARY = "boundary";
    private static final String EXCEPTION = "exception";

    public static void main(final String[] args) {
        final AutoProgressionPerformanceInstrumenterExampleTest test =
                new AutoProgressionPerformanceInstrumenterExampleTest();

        test.testInstrumentedBy(
                NullPerformanceConsumer.<PerformanceSample>instance(),
                StringTableStatsViewer.INSTANCE);

        test.testInstrument(
                NullPerformanceConsumer.<PerformanceSample>instance(),
                StringTableStatsViewer.INSTANCE);
    }

    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentTest() {
        testInstrument(NullPerformanceConsumer.<PerformanceSample>instance(),
                NullPerformanceConsumer.<PerformanceStats>instance());
//        testInstrument(StringCsvSampleViewer.INSTANCE,
//                StringTableStatsViewer.INSTANCE);
    }

    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentedByTest() {
        testInstrumentedBy(NullPerformanceConsumer.<PerformanceSample>instance(),
                NullPerformanceConsumer.<PerformanceStats>instance());
//        testInstrumentedBy(StringCsvSampleViewer.INSTANCE,
//                StringTableStatsViewer.INSTANCE);
    }

    private final AbstractTestable EXCEPTION_TEST = new TestableException();
    private final AbstractTestable BOUNDARY_TEST = new BoundaryTestable();

    private static AutoProgressionPerformanceInstrumenter
                createAutoProgressionPerformanceInstrumenter(String name) {
        return AutoProgressionPerformanceInstrumenter.builder()
                .setName(name)
                .setGarbageCollectorMillis(200)
//                .setGetSamplesUntilTimeout(true)
                .setForcedAssertion(AssertPerformance.withTolerance(5)
                        .assertSpeed(EXCEPTION).fasterThan(BOUNDARY))
                .setTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /** First defines the DefaultPerformanceTimer than instrument it. */
    private void testInstrumentedBy(
            final PerformanceConsumer<PerformanceSample> sampleConsumer,
            final PerformanceConsumer<PerformanceStats> statsConsumer) {
        PerformanceTimerFactory
            .createSingleThreaded()

            .addPerformanceConsumer(sampleConsumer)

            .instrumentedBy(
                    createAutoProgressionPerformanceInstrumenter("InstrumentedBy"))
                .addPerformanceConsumer(statsConsumer)
                .addTest(BOUNDARY, BOUNDARY_TEST)
                .addTest(EXCEPTION, EXCEPTION_TEST)
                .execute()
                .use(AssertPerformance.withTolerance(5)
                    .assertSpeed(BOUNDARY).slowerThan(EXCEPTION));
    }

    /** First defines the instrumenter than set a DefaultPerformanceTimer to it. */
    private void testInstrument(
            final PerformanceConsumer<PerformanceSample> sampleConsumer,
            final PerformanceConsumer<PerformanceStats> statsConsumer) {

            createAutoProgressionPerformanceInstrumenter("Instrument")
                .addTest(BOUNDARY, BOUNDARY_TEST)
                .addTest(EXCEPTION, EXCEPTION_TEST)
                .instrument(PerformanceTimerFactory
                    .createSingleThreaded()
                    .addPerformanceConsumer(sampleConsumer))

                .addPerformanceConsumer(statsConsumer)
                .execute()
                .use(AssertPerformance.withTolerance(5)
                    .assertSpeed(BOUNDARY).slowerThan(EXCEPTION));

    }

    private static class TestableException extends AbstractTestable {
        private final int[] array = new int[10];
        private int counter = 0;

        @Override
        public Object test() {
            counter++;
            try {
                array[counter] = counter;
            } catch (ArrayIndexOutOfBoundsException e) {
                counter = 0;
            }
            return array[counter];
        }
    }

    private static class BoundaryTestable extends AbstractTestable {
        private final int[] array = new int[10];
        private int counter = 0;

        @Override
        public Object test() {
            counter++;
            if (counter < array.length) {
                array[counter] = counter;
            } else {
                counter = 0;
            }
            return array[counter];
        }
    }
}
