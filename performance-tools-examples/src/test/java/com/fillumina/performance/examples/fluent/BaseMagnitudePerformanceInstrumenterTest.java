package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class BaseMagnitudePerformanceInstrumenterTest {
    private static final String CONCATENATION = "concatenation";
    private static final String BUILDER = "builder";

    private PrintOut printOut = new PrintOut();

    public static void main(final String[] args) {
        final BaseMagnitudePerformanceInstrumenterTest test =
                new BaseMagnitudePerformanceInstrumenterTest();
        test.printOut = new PrintOut(true);
        test.test();
    }

    @Test
    public void shouldTheStringConcatenationBeSameThanStringBuilder() {
        test();
    }

    public void test() {
        final DefaultPerformanceTimer pt =
                PerformanceTimerFactory.createSingleThreaded();


        pt.addPerformanceConsumerIf(printOut.isPrintOut(),
                SampleLineStringGenerator.VIEWER);

        pt.instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setBaseAndMagnitude(10_000, 2)
                    .setSamples(100)
                    .build())
                .addTest(CONCATENATION, new AbstractTestable() {
                    private int i;

                    @Override
                    public Object test() {
                        final String str =
                                "This is " +
                                (i++) +
                                " a new " +
                                (i++) +
                                " string.";
                        assertString(str);
                        return str;
                    }

                })
                .addTest(BUILDER, new AbstractTestable() {
                    private int i;

                    @Override
                    public Object test() {
                        final String str = new StringBuilder()
                            .append("This is ")
                            .append(i++)
                            .append(" a new ")
                            .append(i++)
                            .append(" string.")
                            .toString();
                        assertString(str);
                        return str;
                    }
                })

                .addPerformanceConsumerIf(printOut.isPrintOut(),
                        WrapperSpeedStatsTableStringGenerator.VIEWER)
                .addPerformanceConsumer(
                        AssertSpeed.withTolerance(Ratio.percentage(20))
                            .assertOrder(CONCATENATION).sameAs(BUILDER))

                .execute();

    }

    private void assertString(final String str) {
        assertTrue(str.contains("This is"));
    }
}
