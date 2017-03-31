package com.fillumina.performance.speed.stats.instrumenter;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumerChain;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;

/**
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionPerformanceInstrumenterConsumerTest
        extends PerformanceConsumerTestHelper {

    private boolean printout;

    public static void main(final String[] args) {
        final AutoProgressionPerformanceInstrumenterConsumerTest test =
                new AutoProgressionPerformanceInstrumenterConsumerTest();
        test.printout = true;
        test.shouldThePerformanceTimerCallMultipleConsumers();
    }

    @Override
    public void executePerformanceProducerWithConsumers(
            final Iterable<PerformanceConsumer<SpeedStats>> consumers) {

        PerformanceTimerFactory
                .createSingleThreaded()

                .addPerformanceConsumerIf(printout,
                        SampleCsvStringGenerator.VIEWER)

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setSamples(10)
                        .setBaseIterations(10)
                        .setMaxPercentageMargin(100) // I don't care
                        .setIncrementIterations(false)
                        .setAutodiscoverBaseIterations(false)
                        .setEliminateOutliers(false)
                        .setTimeoutSeconds(3)
                        .build())

                .addTest("example", new Testable() {
                    private int counter;

                    @Override
                    public void test() {
                        Sink.drain(counter++);
                    }
                })

                .addPerformanceConsumerIf(printout,
                        WrapperSpeedStatsTableStringGenerator.VIEWER)

                .addPerformanceConsumer(
                        new PerformanceConsumerChain<>(consumers))

                .execute();
    }

}
