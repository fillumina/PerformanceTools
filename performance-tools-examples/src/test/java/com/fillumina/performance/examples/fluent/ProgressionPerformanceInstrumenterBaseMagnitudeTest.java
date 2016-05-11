package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.NullPerformanceSampleConsumer;
import com.fillumina.performance.sample.PerformanceSampleConsumer;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.sample.viewer.StringCsvSampleViewer;
import com.fillumina.performance.stats.NullPerformanceStatsConsumer;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
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
                .test(StringCsvSampleViewer.INSTANCE,
                        StringTableStatsViewer.INSTANCE);
    }

    @Test
    public void shouldTheStringConcatenationBeSameThanStringBuilder() {
        test(NullPerformanceSampleConsumer.INSTANCE,
                NullPerformanceStatsConsumer.INSTANCE);
    }

    public void test(final PerformanceSampleConsumer iterationConsumer,
            final PerformanceStatsConsumer resultConsumer) {
        final PerformanceTimer<Testable> pt =
                PerformanceTimerFactory.createSingleThreaded();

        pt.addTest("string concatenation", new AbstractTestable() {

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

        });

        pt.addTest("string builder", new AbstractTestable() {

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
        });

        pt.addPerformanceSampleConsumer(iterationConsumer);

        pt.instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setTimeout(10, TimeUnit.SECONDS)
                    .setBaseAndMagnitude(10_000, 2)
                    .setSamplesPerStep(15)
                    .build())
                .addPerformanceConsumer(resultConsumer)
                .addPerformanceConsumer(AssertPerformance.withTolerance(10)
                    .assertSpeed("string concatenation").sameAs("string builder"))
                .execute();

    }

    private void assertString(final String str) {
        assertTrue(str.contains("This is"));
    }
}
