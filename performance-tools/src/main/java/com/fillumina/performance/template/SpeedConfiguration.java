package com.fillumina.performance.template;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.BulkTestable;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.instrumenter.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.instrumenter.AutoProgressionPerformanceInstrumenterBuilder;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.concurrent.TimeUnit;

/**
 * Configures the tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class SpeedConfiguration implements Activable {
    /** Number of nanoseconds in a second. */
    private static final long NO_TIMEOUT = Long.MAX_VALUE;

    private final TestConfiguration testConfigurator;


    private boolean active = false;
    private int iterations = -1;
    private int samples = AutoProgressionPerformanceInstrumenterBuilder.SAMPLES;
    private int fractions = 10;
    private long timeoutNs = NO_TIMEOUT;
    private int threads = 1;
    private int workers = 1;
    private boolean incrementIterations = true;
    protected int garbageCollectorMillis = -1;
    private boolean eliminateOutliers = true;
    private double maxPercentageMargin =
            AutoProgressionPerformanceInstrumenterBuilder.MAX_PERCENTAGE_MARGIN;
    private boolean autodiscoverBaseIterations = true;
    private boolean getSamplesUntilTimeout = false;
    private int sampleTimeMillis = 250;

    private PerformanceConsumer<SpeedSample> sampleConsumer =
            NullPerformanceConsumer.<SpeedSample>instance();

    private PerformanceConsumer<SpeedStats> statsConsumer =
            NullPerformanceConsumer.<SpeedStats>instance();

    public SpeedConfiguration(TestConfiguration testConfigurator) {
        this.testConfigurator = testConfigurator;
    }

    /**
     * Configures the used memory test. Used memory is the total memory
     * heap used by the test including those which is freed afterwards.
     */
    public MemConfiguration usedMemTest() {
        return testConfigurator.usedMemTest();
    }

    /**
     * Configures the allocated memory test. Allocated memory is the
     * memory which stays allocated after the test has finished.
     */
    public MemConfiguration allocatedMemTest() {
        return testConfigurator.allocatedMemTest();
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

        if (sampleTimeMillis == 250 && threads > 1) {
            // double default sample time for multi-threading tests
            sampleTimeMillis = 500;
        }

        builder
            .setName(testConfigurator.getTestName())
            .setSamples(samples)
            .setTimeout(timeoutNs, TimeUnit.NANOSECONDS)
            .setIncrementIterations(incrementIterations)
            .setPerformanceStatsConsumer(statsConsumer)
            .setGarbageCollectorMillis(garbageCollectorMillis)
            .setMaxPercentageMargin(maxPercentageMargin)
            .setEliminateOutliers(eliminateOutliers)
            .setAutodiscoverBaseIterations(autodiscoverBaseIterations)
            .setApproximateSampleMillis(sampleTimeMillis)
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

    /** Sets speed test. */
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

    /** Sets a statistics consumer. */
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
        setConcurrencyLevel(Runtime.getRuntime().availableProcessors() * 3 / 2);
        return this;
    }

    /** Auto discover iterations (default yes). */
    public SpeedConfiguration
                setAutodiscoverBaseIterations(boolean autodiscoverBaseIterations) {
        this.autodiscoverBaseIterations = autodiscoverBaseIterations;
        return this;
    }

    /** Continue taking samples until timeout. */
    public SpeedConfiguration
                setGetSamplesUntilTimeout(boolean getSamplesUntilTimeout) {
        this.getSamplesUntilTimeout = getSamplesUntilTimeout;
        return this;
    }

    public SpeedConfiguration setSampleTimeMillis(int sampleTimeMillis) {
        this.sampleTimeMillis = sampleTimeMillis;
        return this;
    }

    /**
     * Sets the number of concurrent threads. It affects both threads and
     * workers.
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
     *
     * @see #setDefaultMultiThreadedMode()
     * @see #setConcurrencyLevel(int)
     */
    public SpeedConfiguration setThreads(final int threads) {
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
     * How many iterations should be executed.
     *
     * @see #setIncrementIterations()
     * @see #setIncrementSamples()
     */
    public SpeedConfiguration setBaseIterations(
            final int baseIterations) {
        this.autodiscoverBaseIterations = false;
        this.iterations = baseIterations;
        return this;
    }

    /**
     * Sets how many samples are taken.
     */
    public SpeedConfiguration setSamples(final int samples) {
        this.samples = samples;
        return this;
    }

    /**
     * How many times tests switches during a sample (default 10).
     * Interleaving tests helps mitigate fast disturbances
     * (mainly background tasks).
     */
    public SpeedConfiguration setFractions(int fractions) {
        this.fractions = fractions;
        return this;
    }

    /**
     * Sets the maximum allowed margin percentage each test has in respect
     * to the slowest. It is the main throttle to use to improve the
     * accuracy of the measure.
     */
    public SpeedConfiguration setMaxPercentageMargin(
            final double maxPercentageMargin) {
        this.maxPercentageMargin = maxPercentageMargin;
        return this;
    }

    /**
     * Set the milliseconds to wait after each set of samples to allow
     * the garbage collector to work. Calling {@code System.gc()} it's
     * just a suggestion to the JVM, by allowing a thread sleep afterwards might
     * increase the probability the garbage collection actually takes place.
     *
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
     * Increments the number of iterations per samples in case a new test
     * should be proven needed (insufficient accuracy detected). This is the
     * default behavior.
     * @see #setIncrementSamples()
     */
    public SpeedConfiguration setIncrementIterations() {
        this.incrementIterations = true;
        return this;
    }

    /**
     * Increments the number of samples if a new test should be proven needed.
     * @see #setIncrementIterations()
     */
    public SpeedConfiguration setIncrementSamples() {
        this.incrementIterations = false;
        return this;
    }

    /**
     * Eliminates samples which are more than 3 times farther to the mean.
     */
    public SpeedConfiguration setEliminateOutliers(boolean eliminateOutliers) {
        this.eliminateOutliers = eliminateOutliers;
        return this;
    }

    /**
     * Sets special configurations needed to run a bulk test.
     * @see BulkTestable
     */
    public SpeedConfiguration setBulkSpecificConfig() {
        setSamples(100);
        setIncrementSamples();
        setGarbageCollectorMillis(100);
        return this;
    }

    /**
     * After how much time the test gives up with an exception.
     * Always use a sensible value because a performance test (even the
     * most obvious ones) can fail for a number of reasons or give strange
     * results that can make the calculations run forever. In this case
     * it's better to have some sort of time limitation.
     */
    public SpeedConfiguration setTimeoutSeconds(
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
                .param("timeout", IntervalUnit.getHelper().toString(timeoutNs))
                .param("threads", threads, -1, "all available")
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
