package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.PerformanceStats;
import com.fillumina.performance.speed.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedTableStringGenerator;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenterBaseMagnitudeTest {

    public static void main(final String[] args) {
        new ProgressionPerformanceInstrumenterBaseMagnitudeTest()
                .test(SampleCsvStringGenerator.VIEWER,
                        SpeedTableStringGenerator.VIEWER);
    }

    @Test
    public void shouldTheStringConcatenationBeSameThanStringBuilder() {
        test(
                NullPerformanceConsumer.<PerformanceSample>instance(),
                NullPerformanceConsumer.<PerformanceStats>instance());
    }

    public void test(
            final PerformanceConsumer<PerformanceSample> iterationConsumer,
            final PerformanceConsumer<PerformanceStats> resultConsumer) {
        final DefaultPerformanceTimer pt =
                PerformanceTimerFactory.createSingleThreaded();


        pt.addPerformanceConsumer(iterationConsumer);

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

                .addPerformanceConsumer(resultConsumer)
                .addPerformanceConsumer(AssertSpeed.withTolerance(15)
                    .assertOrder("string concatenation").sameAs("string builder"))

                .execute();

    }

    private void assertString(final String str) {
        assertTrue(str.contains("This is"));
    }
}
