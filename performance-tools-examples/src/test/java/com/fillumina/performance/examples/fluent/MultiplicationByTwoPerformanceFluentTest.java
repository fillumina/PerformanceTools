 package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.formatter.StringCsvSampleViewer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.formatter.StringTableStatsFormatter;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MultiplicationByTwoPerformanceFluentTest {

    private boolean display = false;

    public static void main(final String[] args) {
        final MultiplicationByTwoPerformanceFluentTest test =
                new MultiplicationByTwoPerformanceFluentTest();
        test.display = true;
        test.executeTest();
    }

    @Test
    public void executeTest() {

        PerformanceTimerFactory.createSingleThreaded()
                .addPerformanceConsumerIf(display,
                        StringCsvSampleViewer.VIEWER)

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                            .setName("Multiplication By Two - fluent")
                            .setTimeout(60, TimeUnit.SECONDS)
                            .setMinConfidence(0.5)
                            .setMaxPercentageMargin(7)
                            .build())

                .addTest("math", new AbstractTestable() {
                    final LinearFeedbackShiftRegister lfsr =
                            new LinearFeedbackShiftRegister(16);

                    @Override
                    public Object test() {
                        return lfsr.next() * 2;
                    }
                })

                .addTest("binary", new AbstractTestable() {
                    final LinearFeedbackShiftRegister lfsr =
                            new LinearFeedbackShiftRegister(16);

                    @Override
                    public Object test() {
                        return lfsr.next() << 1;
                    }
                })

                .addPerformanceConsumerIf(display,
                        StringTableStatsFormatter.VIEWER)

                .execute()

                .check(AssertPerformance.withTolerance(10)
                    .assertSpeed("binary").sameAs("math"))

                .printIf(display);
    }
}
