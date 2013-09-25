package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.LoopPerformancesSequence;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.AbstractInstrumentablePerformanceProducer;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Increments the iterations up to the point when the performances
 * stabilize. It then produces
 * statistics based on the average results of the last round of iterations. It
 * produces accurate measures but may be very long to execute (in that
 * case try to relax the maximum allowed standard deviation or warmup the
 * tests before execution).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceInstrumenter
        extends AbstractInstrumentablePerformanceProducer
            <AutoProgressionPerformanceInstrumenter>
        implements Serializable,
        InstrumentablePerformanceExecutor<AutoProgressionPerformanceInstrumenter>,
        PerformanceExecutorInstrumenter {
    private static final long serialVersionUID = 1L;

    private final ProgressionPerformanceInstrumenter progressionPerformance;

    private List<StandardDeviationConsumer> standardDeviationConsumers =
            new ArrayList<>();

    /**
     * Creates a new instance using a builder with a
     * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     * fluent interface</a></i>.
     */
    public static AutoProgressionPerformanceInstrumenterBuilder builder() {
        return new AutoProgressionPerformanceInstrumenterBuilder();
    }

    public AutoProgressionPerformanceInstrumenter(
            final AutoProgressionPerformanceInstrumenterBuilder builder) {

        final ProgressionPerformanceInstrumenterBuilder ppiBuilder =
                new ProgressionPerformanceInstrumenterBuilder();

        ppiBuilder.setMessage(builder.getMessage());
        ppiBuilder.setBaseAndMagnitude(builder.getBaseIterations(), 8);
        ppiBuilder.setSamplesPerStep(builder.getSamplesPerStep());
        ppiBuilder.setCheckStdDeviation(builder.isCheckStdDeviation());
        ppiBuilder.setTimeoutInNanoseconds(builder.getTimeoutInNanoseconds());

        this.progressionPerformance =
                new ProgressionPerformanceInstrumenter(ppiBuilder) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean stopIterating(
                    final LoopPerformancesSequence performances) {
                final double stdDev =
                        performances.calculateMaximumStandardDeviation();
                callStandardDeviationConsumers(
                        performances.getAverageIterations(),
                        performances.getSamples(),
                        stdDev);
                return stdDev < builder.getMaxStandardDeviation();
            }
        };
    }

    public AutoProgressionPerformanceInstrumenter(
            final ProgressionPerformanceInstrumenter progressionPerformance) {
        this.progressionPerformance = progressionPerformance;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter instrument(
            final InstrumentablePerformanceExecutor<?> performanceExecutor) {
        progressionPerformance.instrument(performanceExecutor);
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
        progressionPerformance.addTest(name, test);
        return this;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter
            addPerformanceConsumer(final PerformanceConsumer... consumers) {
        progressionPerformance.addPerformanceConsumer(consumers);
        return this;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter
            removePerformanceConsumer(final PerformanceConsumer... consumers) {
        progressionPerformance.removePerformanceConsumer(consumers);
        return this;
    }

    @Override
    public LoopPerformancesHolder execute() {
        return progressionPerformance.execute();
    }

    @Override
    public AutoProgressionPerformanceInstrumenter warmup() {
        progressionPerformance.warmup();
        return this;
    }

    /**
     * Adds a {@link StandardDeviationConsumer} that will be called at every
     * step with the average standard deviation of all the {@code samples}
     * of that step.
     * @param consumer the {@link StandardDeviationConsumer}
     * @return  {@code this} to allow for <i>fluent interface</i>
     */
    public AutoProgressionPerformanceInstrumenter addStandardDeviationConsumer(
            final StandardDeviationConsumer consumer) {
        standardDeviationConsumers.add(consumer);
        return this;
    }

    private void callStandardDeviationConsumers(
            final long iterations, final long samples, final double stdDev) {
        for (final StandardDeviationConsumer consumer:
                standardDeviationConsumers) {
            consumer.consume(iterations, samples, stdDev);
        }
    }
}
