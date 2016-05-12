package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.viewer.StringCsvSampleViewer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MultiplicationByTwoPerformanceTest {

    private boolean display = false;

    public static void main(final String[] args) {
        final MultiplicationByTwoPerformanceTest test =
                new MultiplicationByTwoPerformanceTest();
        test.display = true;
        test.executeTest();
    }

    @Test
    public void executeTest() {
        final LinearFeedbackShiftRegister lfsr =
                new LinearFeedbackShiftRegister(16);

        PerformanceTimerFactory.createSingleThreaded()
                .addTest("math", new AbstractTestable() {

                    @Override
                    public Object test() {
                        return lfsr.next() * 2;
                    }
                })

                .addTest("binary", new AbstractTestable() {

                    @Override
                    public Object test() {
                        return lfsr.next() << 1;
                    }
                })

                .addPerformanceConsumerIf(display,
                        StringCsvSampleViewer.INSTANCE)

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                            .setName("Multiplication By Two - fluent")
                            .setTimeout(10, TimeUnit.SECONDS)
                            .build())

                .addPerformanceConsumerIf(display, StringTableStatsViewer.INSTANCE)

                .execute()

                .use(AssertPerformance.withTolerance(10)
                    .assertSpeed("binary").sameAs("math"))

                .printIf(display);
    }
}
