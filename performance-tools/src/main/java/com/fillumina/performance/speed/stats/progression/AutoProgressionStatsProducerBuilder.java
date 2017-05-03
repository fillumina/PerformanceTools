package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.speed.stats.SpeedStats;

public class AutoProgressionStatsProducerBuilder
        extends AbstractProgressionStatsProducerBuilder<
            AutoProgressionStatsProducerBuilder,
            AutoProgressionStatsProducer>{

    private int iterations = 1_000;
    private int samples = -1;
    private boolean incrementIterations = true;
    private double maxPercentageMargin = 5;
    private boolean autodiscoverBaseIterations = true;
    private StatsAssertion<?,SpeedStats> forcedAssertion = null;
    private boolean getSamplesUntilTimeout;
    private int approximateSampleMillis = 250;

    public AutoProgressionStatsProducerBuilder setBaseIterations(
            int iterations) {
        setAutodiscoverBaseIterations(false);
        this.iterations = iterations;
        return this;
    }

    public AutoProgressionStatsProducerBuilder setForcedAssertion(
            StatsAssertion<?,SpeedStats> forcedAssertion) {
        this.forcedAssertion = forcedAssertion;
        return this;
    }

    /**
     * Setting samples to -1 uses an automatic value so that there are
     * at least 2_000 iterations completed (1_000 iterations are needed
     * on default JVM settings to start optimizing the code).
     *
     * @param samples
     * @return
     */
    public AutoProgressionStatsProducerBuilder setSamples(
            int samples) {
        this.samples = samples;
        return this;
    }

    public AutoProgressionStatsProducerBuilder
                setIncrementIterations(boolean incrementIteration) {
        this.incrementIterations = incrementIteration;
        return this;
    }

    public AutoProgressionStatsProducerBuilder
                incrementIterations() {
        this.incrementIterations = true;
        return this;
    }

    public AutoProgressionStatsProducerBuilder
                incrementSamples() {
        this.incrementIterations = false;
        return this;
    }

    public AutoProgressionStatsProducerBuilder
                setMaxPercentageMargin(double maxPercentageMargin) {
        this.maxPercentageMargin = maxPercentageMargin;
        return this;
    }

    public AutoProgressionStatsProducerBuilder
                setAutodiscoverBaseIterations(boolean autodiscoverBaseIterations) {
        this.autodiscoverBaseIterations = autodiscoverBaseIterations;
        return this;
    }

    public AutoProgressionStatsProducerBuilder
                setApproximateSampleMillis(int approximateSampleMillis) {
        this.approximateSampleMillis = approximateSampleMillis;
        return this;
    }

    public AutoProgressionStatsProducerBuilder
                setGetSamplesUntilTimeout(boolean getSamplesUntilTimeout) {
        this.getSamplesUntilTimeout = getSamplesUntilTimeout;
        return this;
    }

    public AutoProgressionStatsProducerBuilder
                setAutoDiscoverSamples(boolean autodiscoverSamples) {
        if (autodiscoverSamples) {
            this.samples = -1;
        } else {
            this.samples = 40;
        }
        return this;
    }

    @Override
    public AutoProgressionStatsProducer build() {
        return new AutoProgressionStatsProducer(
                TN.EMPTY.append(name),
                timeoutNs,
                garbageCollectorMillis,
                filterSamples,
                coolDownCpu,
                iterations,
                samples,
                incrementIterations,
                maxPercentageMargin,
                autodiscoverBaseIterations,
                forcedAssertion,
                getSamplesUntilTimeout,
                approximateSampleMillis,
                performanceStatsConsumers);
    }

}
