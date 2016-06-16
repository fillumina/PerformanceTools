package com.fillumina.performance.accuracy;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.formatter.StringCsvSampleViewer;
import com.fillumina.performance.stats.assertion.AssertSpeedStats;
import com.fillumina.performance.stats.formatter.StringTableStatsFormatter;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Executes some code that can be considered minimal to see how they perform.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MinimumPerformanceGateTest {
    private boolean printout = false;


    public static void main(final String[] args) {
        final MinimumPerformanceGateTest test = new MinimumPerformanceGateTest();
        test.printout = true;
        test.shouldDeadCodeOptimizationBeRecognized();
    }

    @Test
    public void shouldDeadCodeOptimizationBeRecognized() {

        PerformanceTimerFactory.createSingleThreaded()
                .addPerformanceConsumerIf(printout,
                        StringCsvSampleViewer.VIEWER)
                .instrumentedBy(
                        AutoProgressionPerformanceInstrumenter.builder()
                        .setBaseIterations(1_000)
                        .setSamples(100)
                        .setMaxPercentageMargin(10)
                        .setTimeout(2, TimeUnit.MINUTES)
                        .setForcedAssertion(AssertSpeedStats.withTolerance(10)
                                .assertOrder("null").sameAs("dead code"))
                        .build())
                .addTest("null", new AbstractTestable() {
                    @Override
                    public Object test() {
                        return null; // should be optimized
                    }
                })
                .addTest("dead code", new AbstractTestable() {
                    @Override
                    public Object test() {
                        return 3 + 4; // should be optimized
                    }
                })
                .addTest("minimum", new AbstractTestable() {
                    int counter;
                    @Override
                    public Object test() {
                        return ++counter;
                    }
                })
                .addTest("lfsr", new AbstractTestable() {
                    final LinearFeedbackShiftRegister lfsr =
                            new LinearFeedbackShiftRegister();
                    @Override
                    public Object test() {
                        return lfsr.next();
                    }
                })
                .addPerformanceConsumerIf(printout,
                        StringTableStatsFormatter.VIEWER)
                .execute()
                .printIf(printout)
                .check(AssertSpeedStats.withTolerance(10)
                        .assertOrder("null").sameAs("dead code"));
    }
}
