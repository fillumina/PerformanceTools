package com.fillumina.performance.template;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenterBuilder;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.unit.IntervalUnit;
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
public class SpeedConfiguration implements Activable {
    private final TestConfiguration testConfigurator;

    private boolean active = false;
    private int iterations = -1;
    private int samples = AutoProgressionPerformanceInstrumenterBuilder.SAMPLES;
    private int fractions = 10;
    private double minConfidence =
            AutoProgressionPerformanceInstrumenterBuilder.MIN_CONFIDENCE;
    private long timeoutNs = 10_000_000_000L; // 10 seconds
    private int threads = 1;
    private int workers = 1;
    private boolean incrementIterations = true;
    protected int garbageCollectorMillis = -1;
    private boolean eliminateOutliers = true;
    private double maxPercentageMargin =
            AutoProgressionPerformanceInstrumenterBuilder.MAX_PERCENTAGE_MARGIN;
    private boolean autodiscoverBaseIterations = true;
    private boolean getSamplesUntilTimeout = false;

    //TODO not used??
    private PerformanceConsumer<SpeedSample> sampleConsumer =
            NullPerformanceConsumer.<SpeedSample>instance();

    private PerformanceConsumer<SpeedStats> statsConsumer =
            NullPerformanceConsumer.<SpeedStats>instance();

    public SpeedConfiguration(TestConfiguration testConfigurator) {
        this.testConfigurator = testConfigurator;
    }

    public MemConfiguration performUsedMemTest() {
        return testConfigurator.performUsedMemTest();
    }

    public MemConfiguration performAllocatedMemTest() {
        return testConfigurator.performAllocatedMemTest();
    }

    /**
     * Override to return a {@link PerformanceExecutorInstrumenter}
     * other than {@link AutoProgressionPerformanceInstrumenter}.
     * @return null if no instrumenter has to be used.
     */
    protected AutoProgressionPerformanceInstrumenter create(
            PerformanceTimer performanceTimer) {

        performanceTimer.addPerformanceConsumer(sampleConsumer);
        final AutoProgressionPerformanceInstrumenterBuilder builder =
                AutoProgressionPerformanceInstrumenter.builder();

        if (iterations > 0) {
            builder.setBaseIterations(iterations);
        }

        builder
            .setName(testConfigurator.getTestName())
            .setSamples(samples)
            .setMinConfidence(minConfidence)
            .setTimeout(timeoutNs, TimeUnit.NANOSECONDS)
            .setIncrementIterations(incrementIterations)
            .setPerformanceStatsConsumer(statsConsumer)
            .setGarbageCollectorMillis(garbageCollectorMillis)
            .setMaxPercentageMargin(maxPercentageMargin)
            .setEliminateOutliers(eliminateOutliers)
            .setAutodiscoverBaseIterations(autodiscoverBaseIterations)
            .setGetSamplesUntilTimeout(getSamplesUntilTimeout);

        return builder.build().instrument(performanceTimer);
    }

    protected PerformanceTimer createPerformanceTimer() {
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

    public SpeedConfiguration setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    protected SpeedConfiguration setPerformanceSampleConsumer(
            PerformanceConsumer<SpeedSample> sampleConsumer) {
        this.sampleConsumer = sampleConsumer;
        return this;
    }

    public SpeedConfiguration setPerformanceStatsConsumer(
            PerformanceConsumer<SpeedStats> statsPerformanceConsumer) {
        this.statsConsumer = statsPerformanceConsumer;
        return this;
    }

    /**
     * Sets threads and workers to default values for multi
     * threading tests.
     */
    public SpeedConfiguration setDefaultMultiThreadedMode() {
        setConcurrencyLevel(32);
        return this;
    }

    public SpeedConfiguration
                setAutodiscoverBaseIterations(boolean autodiscoverBaseIterations) {
        this.autodiscoverBaseIterations = autodiscoverBaseIterations;
        return this;
    }

    public SpeedConfiguration
                setGetSamplesUntilTimeout(boolean getSamplesUntilTimeout) {
        this.getSamplesUntilTimeout = getSamplesUntilTimeout;
        return this;
    }

    /**
     * Sets the number of concurrent threads working on the test's
 create. It modifies both threads and workers accordingly.
     */
    public SpeedConfiguration setConcurrencyLevel(
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
    public SpeedConfiguration setThreads(
            final int threads) {
        this.threads = threads;
        return this;
    }

    /** Creates as many threads as needed (matching workers). */
    public SpeedConfiguration setUnlimitedThreads() {
        setThreads(-1);
        return this;
    }

    /**
     * Sets how many different tasks will compete for a thread.
     *
     * @see #setDefaultMultiThreadedMode()
     * @see #setConcurrencyLevel(int)
     */
    public SpeedConfiguration setWorkers(
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
    public SpeedConfiguration setBaseIterations(
            final int baseIterations) {
        this.autodiscoverBaseIterations = false;
        this.iterations = baseIterations;
        return this;
    }

    /**
     * Sets how many samples are taken to calculate the statistics at each
     * step.
     */
    public SpeedConfiguration setSamplesPerStep(final int samplesPerStep) {
        this.samples = samplesPerStep;
        return this;
    }

    /** How many times tests switches during a sample (default 100). */
    public SpeedConfiguration setFractions(int fractions) {
        this.fractions = fractions;
        return this;
    }

    /**
     * Sets the maximum allowed standard deviation of the samples taken
     * in one progression.
     */
    public SpeedConfiguration setMaxPercentageMargin(
            final double maxPercentageMargin) {
        this.maxPercentageMargin = maxPercentageMargin;
        return this;
    }

    /**
     * Sets the maximum allowed standard deviation of the samples taken
     * in one progression.
     */
    public SpeedConfiguration setMinConfidence(
            final double minConfidence) {
        // TODO what??
        this.minConfidence = minConfidence;
        return this;
    }

    /**
     * @param incrementIteration if true increments iterations,
     *                           if false increments samples
     */
    public SpeedConfiguration setIncrementIterations() {
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
    public SpeedConfiguration setGarbageCollectorMillis(
            int garbageCollectorMillis) {
        this.garbageCollectorMillis = garbageCollectorMillis;
        return this;
    }

    /**
     * @param incrementIteration if true increments iterations,
     *                           if false increments samples
     */
    public SpeedConfiguration setIncrementSamples() {
        this.incrementIterations = false;
        return this;
    }

    public SpeedConfiguration setEliminateOutliers(boolean eliminateOutliers) {
        this.eliminateOutliers = eliminateOutliers;
        return this;
    }

    /**
     * After how much time the test gives up with an exception.
     * Always use a sensible param because a performance test (even the
     * most obvious ones) can fail for a number of reasons or give strange
     * results that can make the calculations run forever. In this case
     * it's better to have some sort of time limitation.
     *     */
    public SpeedConfiguration setTimeoutSeconds(
            final long timeoutSeconds) {
        this.timeoutNs = TimeUnit.NANOSECONDS.convert(timeoutSeconds,
                TimeUnit.SECONDS);
        return this;
    }

    /**
     * After how much time the test gives up with an exception.
     * Always use a sensible param because a performance test (even the
     * most obvious ones) can fail for a number of reasons or give strange
     * results that can make the calculations run forever. In this case
     * it's better to have some sort of time limitation.
     */
    public SpeedConfiguration setTimeout(final long value,
            final TimeUnit unit) {
        this.timeoutNs = TimeUnit.NANOSECONDS.convert(value, unit);
        return this;
    }

    @Override
    public String toString() {
        return new TableFormatter()
                .param("iterations", iterations,
                        -1, "automatic")
                .param("samples", samples)
                .param("fractions", fractions)
                .param("minConfidence", minConfidence)
                .param("timeout", IntervalUnit.FORMATTER.toString(timeoutNs))
                .param("threads", threads)
                .param("workers", workers)
                .param("incrementIterations", incrementIterations)
                .param("autodiscoverBaseIterations", autodiscoverBaseIterations)
                .param("getSamplesUntilTimeout", getSamplesUntilTimeout)
                .param("garbageCollectorMills", garbageCollectorMillis,
                        -1, "no GC required")
                .param("eliminateOutliers", eliminateOutliers)
                .param("maxPercentageMargin", maxPercentageMargin + " %")
                .toString();
    }

}
