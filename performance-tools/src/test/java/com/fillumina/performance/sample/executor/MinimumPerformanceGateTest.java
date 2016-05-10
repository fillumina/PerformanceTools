package com.fillumina.performance.sample.executor;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.viewer.StringCsvSampleViewer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
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
                .addTest("reference", new AbstractTestable() {
                    final LinearFeedbackShiftRegister lfsr =
                            new LinearFeedbackShiftRegister();
                    int counter = 0;
                    @Override
                    public Object test() {
                        counter += lfsr.next();
                        return counter;
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
                .addPerformanceSampleConsumerIf(printout,
                        StringCsvSampleViewer.INSTANCE)
                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setBaseIterations(1_000)
                        .setBaseSamples(100)
                        .setTimeout(2, TimeUnit.MINUTES)
                        .setAddBaselineTest(false)
//                        .setForcedAssertion(AssertPerformance
//                                .withTolerance(10)
//                                .assertPercentage("dead code").sameAs(0))
                        .build())
                .addPerformanceConsumerIf(printout,
                        StringTableStatsViewer.INSTANCE)
                .execute()
                .printIf(printout)
                .use(AssertPerformance.withTolerance(10)
                        .assertSpeed("null").sameAs("dead code"));
    }
}
