package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.speed.sample.PerformanceTimerFactory;
import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.AssertSpeed;
import com.fillumina.performance.speed.stats.progression.RepeatingStrategy;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 * Shows both ways to define an auto progression performance run:
 <ul>
 * <li>By defining the
 *      {@link com.fillumina.performance.speed.sample.DefaultPerformanceTimer}
 *      first and than instrument it
 *      with the {@link RepeatingStrategy}.</li>
 * <li>By defining the {@link RepeatingStrategy} first
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

    private final Testable EXCEPTION_TEST = new TestableException();
    private final Testable BOUNDARY_TEST = new BoundaryTestable();

    private PrintOut printout = new PrintOut();

    public static void main(final String[] args) {
        final ExceptionVsBoundaryPerformanceTest test =
                new ExceptionVsBoundaryPerformanceTest();

        test.printout = new PrintOut(true);
//        run.testInstrumentedBy();
        test.testInstrument();
    }

    //https://www.microsoftpressstore.com/articles/article.aspx?p=2233328&seqNum=7
    // FIXME iterations was negative.. check that "invalid iteration decimal = -1151354296"
    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentTest() {
        testInstrument();
    }

    //FIXME: 'boundary' (8.4174 +/- 0.1646 (33 samples) ns) expected greater than 'exception' (10.2388 +/- 0.2277 (33 samples) ns)  with a tolerance of 10.000 %
    @Test
    public void boundaryCheckAgainstOOBExceptionInstrumentedByTest() {
        testInstrumentedBy();
    }

    private static RepeatingStrategy
                createAutoProgressionPerformanceInstrumenter(String name) {
        return RepeatingStrategy.statsProducerBuilder()
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
                .use(AssertSpeed.withTolerance(Ratio.percentage(10))
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
                .use(AssertSpeed.withTolerance(Ratio.percentage(10))
                    .assertOrder(BOUNDARY).greaterThan(EXCEPTION));

    }

    private static class TestableException extends Testable {
        private final int[] array = new int[10];
        private int counter = 0;

        @Override
        public void run() {
            counter++;
            try {
                array[counter] = counter;
            } catch (ArrayIndexOutOfBoundsException e) {
                counter = 0;
            }
            Sink.drain(array[counter]);
        }
    }

    private static class BoundaryTestable extends Testable {
        private final int[] array = new int[10];
        private int counter = 0;

        @Override
        public void run() {
            counter++;
            if (counter < array.length) {
                array[counter] = counter;
            } else {
                counter = 0;
            }
            Sink.drain(array[counter]);
        }
    }
}
