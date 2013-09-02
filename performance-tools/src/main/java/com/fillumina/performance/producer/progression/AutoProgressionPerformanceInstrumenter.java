package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.LoopPerformancesSequence;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Increments the iterations up to the point when the performances
 * stabilize. It then produces
 * statistics based on the average results of the last iterations. It
 * produces accurate measures but may be very long to execute.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceInstrumenter
        implements Serializable,
        InstrumentablePerformanceExecutor<AutoProgressionPerformanceInstrumenter>,
        PerformanceExecutorInstrumenter {
    private static final long serialVersionUID = 1L;

    private final ProgressionPerformanceInstrumenter progressionSerie;
    private List<StandardDeviationConsumer> standardDeviationConsumers =
            new ArrayList<>();

    public static AutoProgressionPerformanceInstrumenterBuilder builder() {
        return new AutoProgressionPerformanceInstrumenterBuilder();
    }

    public AutoProgressionPerformanceInstrumenter(
            final AutoProgressionPerformanceInstrumenterBuilder builder) {

        final ProgressionPerformanceInstrumenterBuilder ppiBuilder =
                new ProgressionPerformanceInstrumenterBuilder();

        ppiBuilder.setMessage(builder.getMessage());
        ppiBuilder.setBaseAndMagnitude(builder.getBaseIterations(), 8);
        ppiBuilder.setSamplesPerMagnitude(builder.getSamplesPerMagnitude());
        ppiBuilder.setCheckStdDeviation(builder.isCheckStdDeviation());
        ppiBuilder.setTimeoutInNanoseconds(builder.getTimeoutInNanoseconds());

        this.progressionSerie = new ProgressionPerformanceInstrumenter(ppiBuilder) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean stopIterating(
                    final LoopPerformancesSequence performances) {
                final double stdDev =
                        performances.calculateMaximumStandardDeviation();
                callStandardDeviationConsumers(performances.getAverageIterations(),
                        performances.getSamples(), stdDev);
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
    public AutoProgressionPerformanceInstrumenter instrument(
            final InstrumentablePerformanceExecutor<?> performanceExecutor) {
        progressionSerie.instrument(performanceExecutor);
        return this;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter ignoreTest(
            final String name,
            final Runnable test) {
        return this;
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
    public AutoProgressionPerformanceInstrumenter
            removePerformanceConsumer(final PerformanceConsumer... consumers) {
        progressionSerie.removePerformanceConsumer(consumers);
        return this;
    }

    @Override
    public LoopPerformancesHolder execute() {
        return progressionSerie.execute();
    }

    @Override
    public AutoProgressionPerformanceInstrumenter warmup() {
        progressionSerie.warmup();
        return this;
    }

    public AutoProgressionPerformanceInstrumenter addStandardDeviationConsumer(
            final StandardDeviationConsumer consumer) {
        standardDeviationConsumers.add(consumer);
        return this;
    }

    private void callStandardDeviationConsumers(
            final long iterations, final long samples, final double stdDev) {
        for (final StandardDeviationConsumer consumer: standardDeviationConsumers) {
            consumer.consume(iterations, samples, stdDev);
        }
    }
}
