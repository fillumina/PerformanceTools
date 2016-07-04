package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedTableStringGenerator;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class BaseMagnitudePerformanceInstrumenterTest {

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
                    .setTimeout(20, TimeUnit.SECONDS)
                    .setBaseAndMagnitude(10_000, 2)
                    .setSamples(100)
                    .build())
                .addTest("string concatenation", new AbstractTestable() {

                    @Override
                    public Object test() {
                        final String str = "This " + "is " +
                                System.currentTimeMillis() +
                                "a " + "new " +
                                System.currentTimeMillis() +
                                "string.";
                        assertString(str);
                        return str;
                    }

                })
                .addTest("string builder", new AbstractTestable() {

                    @Override
                    public Object test() {
                        final String str = new StringBuilder()
                            .append("This ")
                            .append("is ")
                            .append(System.currentTimeMillis())
                            .append("a ")
                            .append("new ")
                            .append(System.currentTimeMillis())
                            .append("string.")
                            .toString();
                        assertString(str);
                        return str;
                    }
                })

                .addPerformanceConsumerIf(printOut.isPrintOut(),
                        SpeedTableStringGenerator.VIEWER)
                .addPerformanceConsumer(AssertSpeed.withTolerancePercentage(20)
                    .assertOrder("string concatenation").sameAs("string builder"))

                .execute();

    }

    private void assertString(final String str) {
        assertTrue(str.contains("This is"));
    }
}
