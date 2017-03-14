package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.AbstractTestable;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.speed.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 * Executes some code that can be considered minimal to see how they perform.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MinimumPerformanceGateTest {
    private Appendable printout = null;

    public static void main(final String[] args) {
        final MinimumPerformanceGateTest test = new MinimumPerformanceGateTest();
        test.printout = System.out;
        test.shouldDeadCodeOptimizationBeRecognized();
    }

    @Test
    public void shouldDeadCodeOptimizationBeRecognized() {

        PerformanceTimerFactory.createSingleThreaded()
                .addPerformanceConsumer(
                        SampleCsvStringGenerator.appendTo(printout))
                .instrumentedBy(
                        AutoProgressionPerformanceInstrumenter.builder()
                        .setBaseIterations(1_000)
                        .setSamples(100)
                        .setMaxPercentageMargin(10)
                        .setForcedAssertion(
                                AssertSpeed.withTolerance(Ratio.percentage(10))
                                .assertOrder("null").sameAs("dead code"))
                        .build())
                .addTest("null", new AbstractTestable() {
                    @Override
                    public void test() {
                        Sink.drain(null); // should be optimized out
                    }
                })
                .addTest("dead code", new AbstractTestable() {
                    @Override
                    public void test() {
                        Sink.drain(3 + 4); // should be optimized out
                    }
                })
                .addTest("minimum", new AbstractTestable() {
                    int counter;
                    @Override
                    public void test() {
                        Sink.drain(++counter);
                    }
                })
                .addTest("lfsr", new AbstractTestable() {
                    final LinearFeedbackShiftRegister lfsr =
                            new LinearFeedbackShiftRegister();
                    @Override
                    public void test() {
                        Sink.drain(lfsr.next());
                    }
                })
                .addPerformanceConsumer(WrapperSpeedStatsTableStringGenerator.appendTo(printout))
                .execute()
                .checkAndPrint(printout,
                        AssertSpeed.withTolerance(Ratio.percentage(10))
                        .assertOrder("null").sameAs("dead code"));
    }
}
