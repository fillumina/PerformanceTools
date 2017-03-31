package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.instrumenter.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.rnd.Lfsr;
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

    private volatile int x = 1;
    private volatile int y = 2;

    @Test
    public void shouldDeadCodeOptimizationBeRecognized() {

        PerformanceTimerFactory.createSingleThreaded()
                .addPerformanceConsumer(
                        SampleLineStringGenerator.appendTo(printout))
                .instrumentedBy(
                        AutoProgressionPerformanceInstrumenter.builder()
                        .build())
                .addTest("minimum", new Testable() {
                    @Override
                    public void test() {
                        Sink.drain(x + y);
                    }
                })
                .addTest("lfsr", new Testable() {
                    private Lfsr lfsr = new Lfsr();
                    @Override
                    public void test() {
                        Sink.drain(lfsr.next());
                    }
                })
                .addPerformanceConsumer(WrapperSpeedStatsTableStringGenerator
                        .appendTo(printout))
                .execute()
                .checkAndPrint(printout,
                        AssertSpeed.withTolerance(Ratio.percentage(10))
                        .assertOrder("minimum").lessThan("lfsr"));
    }
}
