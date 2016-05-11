package com.fillumina.performance.template;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.NullPerformanceSampleConsumer;
import com.fillumina.performance.sample.PerformanceSampleConsumer;
import com.fillumina.performance.sample.PerformanceSampleProducer;
import com.fillumina.performance.stats.NullPerformanceStatsConsumer;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import java.util.concurrent.TimeUnit;

/**
 * Configures the tests using a <i>fluent interface</i>.
 * <p>
 * This configuration actually defaults to:
 * <ul>
 * <li>{@code baseIterations} = 1_000
 * <li>{@code maxStandardDeviation} = 10
 * <li>{@code message} = ""
 * <li>{@code standardDeviationConsumers} = empty
 * <li>{@code timeoutSeconds} = 10
 * <li>{@code threads} = 1 (means single thread)
 * <li>{@code workers} = 1
 * <li>no iteration consumers.
 * </ul>
 * <p>
 *
 * @author Francesco Illuminati
 */
public class TestConfigurator {
        // TODO add a maximum number of cycles
        // TODO add warmup
        // TODO add a memory check

    private String message = "";
    private int iterations = 1_000;
    private int samples = 30;
    private int fractions = 100;
    private double minConfidence = .90;
    private long timeoutNs = 10_000_000_000L; // 10 seconds
    private int threads = 1;
    private int workers = 1;
    private boolean incrementIterations = true;
    private int garbageCollectorMillis = -1;
    private boolean eliminateOutliers = true;
    private double maxPercentageMargin = 0.05;

    private PerformanceSampleConsumer sampleConsumer =
            NullPerformanceSampleConsumer.INSTANCE;
    private PerformanceStatsConsumer performanceStatsConsumer =
            NullPerformanceStatsConsumer.INSTANCE;

    /**
     * Override to return a {@link PerformanceExecutorInstrumenter}
     * other than {@link AutoProgressionPerformanceInstrumenter}.
     * @return null if no instrumenter has to be used.
     */
    protected AutoProgressionPerformanceInstrumenter create(
            PerformanceSampleProducer producer) {

        producer.addPerformanceSampleConsumer(sampleConsumer);

        return AutoProgressionPerformanceInstrumenter.builder()
                    .setName(message)
                    .setBaseIterations(iterations)
                    .setBaseSamples(samples)
                    .setMinConfidence(minConfidence)
                    .setTimeout(timeoutNs, TimeUnit.NANOSECONDS)
                    .setIncrementIterations(incrementIterations)
                    .setPerformanceStatsConsumer(performanceStatsConsumer)
                    .setGarbageCollectorMillis(garbageCollectorMillis)
                    .setMaxPercentageMargin(maxPercentageMargin)
                    .setEliminateOutliers(eliminateOutliers)
                    .build()
                .instrument(producer);
    }

    protected DefaultPerformanceTimer createPerformanceTimer() {
        if (threads == 1) {
            return PerformanceTimerFactory.createSingleThreaded(fractions);
        }
        return PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(threads)
                .setWorkers(workers)
                // timeout is managed in the instrumenter
                .setTimeout(timeoutNs, TimeUnit.NANOSECONDS)
                .build();
    }

    protected TestConfigurator setPerformanceSampleConsumer(
            final PerformanceSampleConsumer sampleConsumer) {
        this.sampleConsumer = sampleConsumer;
        return this;
    }

    /**
     * Sets threads and workers to default values for multi
     * threading tests.
     */
    public TestConfigurator setDefaultMultiThreadedMode() {
        setConcurrencyLevel(32);
        return this;
    }

    /**
     * Sets the number of concurrent threads working on the test's
 create. It modifies both threads and workers accordingly.
     */
    public TestConfigurator setConcurrencyLevel(
            final int concurrencyLevel) {
        if (concurrencyLevel > 0) {
            setThreads(-1);
            setWorkers(concurrencyLevel);
        }
        return this;
    }

    /**
     * How many threads should be created.
     * @see #setDefaultMultiThreadedMode()
     * @see #setConcurrencyLevel(int)
     */
    public TestConfigurator setThreads(
            final int threads) {
        this.threads = threads;
        return this;
    }

    /** Creates as many threads as needed (matching workers). */
    public TestConfigurator setUnlimitedThreads() {
        setThreads(-1);
        return this;
    }

    /**
     * Sets how many different tasks will compete for a thread.
     *
     * @see #setDefaultMultiThreadedMode()
     * @see #setConcurrencyLevel(int)
     */
    public TestConfigurator setWorkers(
            final int workers) {
        this.workers = workers;
        return this;
    }

    /**
     * How many iterations should be executed in the first step of an
     * auto progression. If the results will have more than the specified
     * standard deviation a new progression will be executed with more
     * iterations to try to stabilize the results.
     */
    public TestConfigurator setBaseIterations(
            final int baseIterations) {
        this.iterations = baseIterations;
        return this;
    }

    /**
     * Sets how many samples are taken to calculate the statistics at each
     * step.
     */
    public TestConfigurator setSamplesPerStep(final int samplesPerStep) {
        this.samples = samplesPerStep;
        return this;
    }

    /** How many times tests switches during a sample (default 100). */
    public TestConfigurator setFractions(int fractions) {
        this.fractions = fractions;
        return this;
    }

    /**
     * Sets the maximum allowed standard deviation of the samples taken
     * in one progression.
     */
    public TestConfigurator setMaxPercentageMargin(
            final double maxPercentageMargin) {
        this.maxPercentageMargin = maxPercentageMargin;
        return this;
    }

    /**
     * Sets the maximum allowed standard deviation of the samples taken
     * in one progression.
     */
    public TestConfigurator setMinConfidence(
            final double minConfidence) {
        // TODO what??
        this.minConfidence = minConfidence;
        return this;
    }

    /**
     * Sets the message that may be shown on the output viewers or
     * used in assertions.
     */
    public TestConfigurator setMessage(
            final String message) {
        this.message = message;
        return this;
    }

    /**
     * @param incrementIteration if true increments iterations,
     *                           if false increments samples
     */
    public TestConfigurator setIncrementIterations() {
        this.incrementIterations = true;
        return this;
    }

    /**
     * Set the milliseconds to wait after each set of samples to allow
     * the gargbage collector to work.
     * @param garbageCollectorMillis -1 disable garbage collector (default)
     *                               otherwise how many milliseconds to wait
     *                               for the java garbage collector to do its job.
     */
    public TestConfigurator setGarbageCollectorMillis(
            int garbageCollectorMillis) {
        this.garbageCollectorMillis = garbageCollectorMillis;
        return this;
    }

    /**
     * @param incrementIteration if true increments iterations,
     *                           if false increments samples
     */
    public TestConfigurator setIncrementSamples() {
        this.incrementIterations = false;
        return this;
    }

    public TestConfigurator setEliminateOutliers(boolean eliminateOutliers) {
        this.eliminateOutliers = eliminateOutliers;
        return this;
    }

    /**
     * After how much time the test gives up with an exception.
     * Always use a sensible value because a performance test (even the
     * most obvious ones) can fail for a number of reasons or give strange
     * results that can make the calculations run forever. In this case
     * it's better to have some sort of time limitation.
     */
    public TestConfigurator setTimeoutSeconds(
            final long timeoutSeconds) {
        this.timeoutNs = TimeUnit.NANOSECONDS.convert(timeoutSeconds,
                TimeUnit.SECONDS);
        return this;
    }

    /**
     * After how much time the test gives up with an exception.
     * Always use a sensible value because a performance test (even the
     * most obvious ones) can fail for a number of reasons or give strange
     * results that can make the calculations run forever. In this case
     * it's better to have some sort of time limitation.
     */
    public TestConfigurator setTimeout(final long value,
            final TimeUnit unit) {
        this.timeoutNs = TimeUnit.NANOSECONDS.convert(value, unit);
        return this;
    }

    public TestConfigurator setLoopPerformanceConsumer(
            PerformanceStatsConsumer loopPerformanceConsumer) {
        this.performanceStatsConsumer = loopPerformanceConsumer;
        return this;
    }
}
