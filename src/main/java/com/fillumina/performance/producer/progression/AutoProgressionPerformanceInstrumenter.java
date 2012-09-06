package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.LoopPerformancesSequence;
import com.fillumina.performance.producer.PerformanceExecutor;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.Instrumentable;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import java.io.Serializable;

/**
 * Increments the iterations up to the point when the performances
 * stabilize. It then produces
 * statistics based on the average results of the last iterations. It
 * produces accurate measures but may be very long to execute.
 *
 * @author fra
 */
public class AutoProgressionPerformanceInstrumenter
        implements Serializable, Instrumentable,
        PerformanceExecutor<AutoProgressionPerformanceInstrumenter> {
    private static final long serialVersionUID = 1L;

    private final ProgressionPerformanceInstrumenter progressionSerie;

    public static AutoProgressionPerformanceInstrumenterBuilder builder() {
        return new AutoProgressionPerformanceInstrumenterBuilder();
    }

    public AutoProgressionPerformanceInstrumenter(
            final AutoProgressionPerformanceInstrumenterBuilder builder) {
        final ProgressionPerformanceInstrumenterBuilder ppiBuilder =
                new ProgressionPerformanceInstrumenterBuilder();
        ppiBuilder.instrument(builder.getPerformanceExecutor());
        ppiBuilder.setBaseAndMagnitude(builder.getBase(), 10);
        ppiBuilder.setSamplePerMagnitude(builder.getSamplesPerMagnitude());
        ppiBuilder.setTimeoutInNanoseconds(builder.getTimeoutInNanoseconds());

        this.progressionSerie = new ProgressionPerformanceInstrumenter(ppiBuilder) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean stopIterating(LoopPerformancesSequence serie) {
                final double stdDev = serie.calculateMaximumStandardDeviation();
                return stdDev < builder.getMaxStandardDeviation();
            }
        };
    }

    public AutoProgressionPerformanceInstrumenter(
            final ProgressionPerformanceInstrumenter progressionSerie) {
        this.progressionSerie = progressionSerie;
    }

    @Override
    public <T extends PerformanceExecutorInstrumenter> T
            instrumentedBy(final T instrumenter) {
        instrumenter.instrument(progressionSerie);
        return instrumenter;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter addTest(
            final String name,
            final Runnable test) {
        progressionSerie.addTest(name, test);
        return this;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter
            addPerformanceConsumer(final PerformanceConsumer... consumers) {
        progressionSerie.addPerformanceConsumer(consumers);
        return this;
    }

    @Override
    public LoopPerformancesHolder execute() {
        return progressionSerie.execute();
    }
}
