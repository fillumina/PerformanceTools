package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumerChain;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.speed.stats.PerformanceConsumerTestHelper;
import com.fillumina.performance.speed.stats.PerformanceStats;
import com.fillumina.performance.speed.stats.strgen.SpeedTableStringGenerator;

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
            final Iterable<PerformanceConsumer<PerformanceStats>> consumers) {

        PerformanceTimerFactory
                .createSingleThreaded()

                .addPerformanceConsumerIf(printout,
                        SampleCsvStringGenerator.VIEWER)

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setSamples(10)
                        .setMaxPercentageMargin(30)
                        .setTimeoutSeconds(5)
                        .build())

                .addTest("example", new AbstractTestable() {
                    private int counter;

                    @Override
                    public Object test() {
                        return counter++;
                    }
                })

                .addPerformanceConsumerIf(printout,
                        SpeedTableStringGenerator.VIEWER)

                .addPerformanceConsumer(
                        new PerformanceConsumerChain<>(consumers))

                .execute();
    }

}
