package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import org.junit.Test;

/**
 * Shows both ways to define an auto progression performance test:
 * <ul>
 * <li>By defining the
 *      {@link com.fillumina.performance.speed.sample.DefaultPerformanceTimer}
 *      first and than instrument it
 *      with the {@link AutoProgressionPerformanceInstrumenter}.</li>
 * <li>By defining the {@link AutoProgressionPerformanceInstrumenter} first
 *      and than set a
 *      {@link com.fillumina.performance.speed.sample.DefaultPerformanceTimer}
 *      to it.</li>
 * </ul>
 *
 * @author Francesco Illuminati
 */
public class ExceptionVsBoundaryPerformanceTest {
    private static final String BOUNDARY = "boundary";
    private static final String EXCEPTION = "exception";

    private final AbstractTestable EXCEPTION_TEST = new TestableException();
    private final AbstractTestable BOUNDARY_TEST = new BoundaryTestable();

    private PrintOut printout = new PrintOut();

    public static void main(final String[] args) {
        final ExceptionVsBoundaryPerformanceTest test =
                new ExceptionVsBoundaryPerformanceTest();

        test.printout = new PrintOut(true);
//        test.testInstrumentedBy();
        test.testInstrument();
    }

    //https://www.microsoftpressstore.com/articles/article.aspx?p=2233328&seqNum=7
    // TODO iterations was negative.. check that
    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentTest() {
        testInstrument();
    }

    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentedByTest() {
        testInstrumentedBy();
    }

    private static AutoProgressionPerformanceInstrumenter
                createAutoProgressionPerformanceInstrumenter(String name) {
        return AutoProgressionPerformanceInstrumenter.builder()
                .setName(name)
                //.setGarbageCollectorMillis(200)
                .setMaxPercentageMargin(10)
                .build();
    }

    /** First defines the DefaultPerformanceTimer than instrument it. */
    private void testInstrumentedBy() {
        PerformanceTimerFactory
            .createSingleThreaded()

            .addPerformanceConsumerIf(printout.isPrintOut(),
                    SampleLineStringGenerator.VIEWER)

            .instrumentedBy(
                    createAutoProgressionPerformanceInstrumenter("InstrumentedBy"))
                .addPerformanceConsumerIf(printout.isPrintOut(),
                        WrapperSpeedStatsTableStringGenerator.VIEWER)
                .addTest(BOUNDARY, BOUNDARY_TEST)
                .addTest(EXCEPTION, EXCEPTION_TEST)
                .execute()
                .use(AssertSpeed.withTolerance(10)
                    .assertOrder(BOUNDARY).greaterThan(EXCEPTION));
    }

    /** First defines the instrumenter than set a DefaultPerformanceTimer to it. */
    private void testInstrument() {

            createAutoProgressionPerformanceInstrumenter("Instrument")
                .addTest(EXCEPTION, EXCEPTION_TEST)
                .addTest(BOUNDARY, BOUNDARY_TEST)
                .instrument(PerformanceTimerFactory
                    .createSingleThreaded()
                    .addPerformanceConsumerIf(printout.isPrintOut(),
                            SampleLineStringGenerator.VIEWER))

                .addPerformanceConsumerIf(printout.isPrintOut(),
                        WrapperSpeedStatsTableStringGenerator.VIEWER)
                .execute()
                .use(AssertSpeed.withTolerance(10)
                    .assertOrder(BOUNDARY).greaterThan(EXCEPTION));

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
