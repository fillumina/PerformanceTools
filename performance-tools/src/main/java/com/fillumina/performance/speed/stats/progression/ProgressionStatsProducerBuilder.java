package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.TName;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class ProgressionStatsProducerBuilder
        extends AbstractProgressionStatsProducerBuilder<
            ProgressionStatsProducerBuilder,
            ProgressionStatsProducer>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[] iterationsProgression = new int[]{1_000, 10_000, 100_000};
    private int samples = 30;

    /** Creates a builder with a default progression. */
    public ProgressionStatsProducerBuilder() {
        super();
    }

    /** Sets the iterations to be performed at each step. */
    public ProgressionStatsProducerBuilder setIterationProgression(
            final int... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
        return this;
    }

    /** Sets the samples to be collected for each test. */
    public ProgressionStatsProducerBuilder setSamples(
            final int samplesPerStep) {
        this.samples = samplesPerStep;
        return this;
    }

    /**
     * Defines a progression by inserting a starting number and
     * the number of times this number should be increased of magnitude
     * (multiplied by 10).
     * <br>
     * i.e.:
     * <pre>
     * base=100, magnitude=3 : 100, 1_000, 10_000
     * base=20,  magnitude=2 : 20, 200
     * </pre>
     * <br>
     */
    public ProgressionStatsProducerBuilder setBaseAndMagnitude(
            final long baseIterations,
            final int maximumMagnitude) {
        iterationsProgression = new int[maximumMagnitude];
        for (int magnitude = 0; magnitude < maximumMagnitude; magnitude++) {
            iterationsProgression[magnitude] =
                    calculateIterationsProgression(baseIterations, magnitude);
        }
        return this;
    }

    private static int calculateIterationsProgression(
            final long baseIterations,
            final int magnitude) {
        return (int) Math.round(baseIterations * Math.pow(10, magnitude));
    }

    @Override
    public ProgressionStatsProducer build() {
        return new ProgressionStatsProducer(
                TName.EMPTY.append(name),
                timeoutNs,
                garbageCollectorMillis,
                filterSamples,
                coolDownCpu,
                iterationsProgression,
                samples,
                performanceStatsConsumers);
    }
}
