package com.fillumina.performance.examples;

import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.executor.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.executor.stats.producer.RequiredMarginStrategy;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.PerformanceBuilder;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.stats.Ratio;

/**
 * Executes some code that can be considered minimal to see how they perform.
 * It uses a builder instead of a template.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MinimumPerformanceGateTest {
    private Appendable printout = null;

    public static void main(final String[] args) {
        final MinimumPerformanceGateTest test = new MinimumPerformanceGateTest();
        test.printout = System.out;
        test.usingBuilder();
    }

    private volatile int x = 1;
    private volatile int y = 2;
    private final Lfsr lfsr = new Lfsr();

    public void usingBuilder() {

        PerformanceBuilder
                .config()
                    .speedConfig()
                        .setFixedSamples(5)
                    .end()
                .tests()
                    .addTest("minimum", () -> Sink.drain(x + y))
                    .addTest("lfsr", () -> Sink.drain(lfsr.next()))
                .end()
                .assertions()
                    .avgTime()
                        .tolerance(Ratio.percentage(10))
                        .order("minimum").lessThan("lfsr").end()
                    .end()
                .end()
                .executeWithFullOutput();
    }

    public void usingLowLevelAPI() {

        PerformanceTimerFactory.createSingleThreaded()
                .addConsumer(SampleLineStringGenerator.VIEWER)
                .instrumentedBy(RequiredMarginStrategy.createStatsProducer(
                        Ratio.percentage(10)))
                .addTest("minimum", () -> Sink.drain(x + y))
                .addTest("lfsr", () -> Sink.drain(lfsr.next()))
                .addConsumer(TimeStatsStringGeneratorSelector
                        .appendTo(printout, Ratio.P_95))
                .execute()
                .getFirstStatsHolder()
                .checkAndAppendTo(printout,
                        Assertions.withTolerance(Ratio.percentage(10))
                        .assertOrder("minimum").lessThan("lfsr"));
    }
}
