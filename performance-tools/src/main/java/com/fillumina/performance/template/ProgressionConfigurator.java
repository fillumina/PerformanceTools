package com.fillumina.performance.template;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.progression.StandardDeviationConsumer;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.TimeUnit;

/**
 * Configures the tests using a <i>fluent interface</i>.
 * <p>
 * This is in fact a builder but it's used mainly as a configurator in the
 * templates so the actual name was preferred.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionConfigurator {
    private long baseIterations = 1_000;
    private double maxStandardDeviation = 10;
    private String message = "";
    private boolean printOutStdDeviation;
    private int timeoutSeconds = 10;
    private int threads = 1;
    private int workers = 1;
    private PerformanceConsumer iterationConsumer =
            NullPerformanceConsumer.INSTANCE;

    /**
     * Override to return a {@link PerformanceExecutorInstrumenter}
     * other than {@link AutoProgressionPerformanceInstrumenter}.
     * @return null if no instrumenter has to be used.
     */
    protected AutoProgressionPerformanceInstrumenter create() {
        final PerformanceTimer pe = createPerformanceTimer();

        pe.addPerformanceConsumer(iterationConsumer);

        return AutoProgressionPerformanceInstrumenter.builder()
                    .setBaseIterations(baseIterations)
                    .setSamplesPerStep(10)
                    .setMaxStandardDeviation(maxStandardDeviation)
                    .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                    .setMessage(message)
                    .build()
                .instrument(pe)
                .addStandardDeviationConsumer(new StandardDeviationConsumer() {

            @Override
            public void consume(final long iterations,
                    final long samples, final double stdDev) {
                if (printOutStdDeviation) {
                    System.out.println(new StringBuilder()
                            .append("Iterations: ").append(iterations)
                            .append("\tSamples: ").append(samples)
                            .append("\tStandard Deviation: ").append(stdDev)
                            .toString());
                }
            }
        });
    }

    private PerformanceTimer createPerformanceTimer() {
        if (threads == 1) {
            return PerformanceTimerFactory.createSingleThreaded();
        }
        return PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(threads)
                .setWorkers(workers)
                // timeout is managed in the instrumenter
                .setTimeout(100_000, TimeUnit.SECONDS)
                .buildPerformanceTimer();
    }

    protected ProgressionConfigurator setIterationConsumer(
            final PerformanceConsumer iterationConsumer) {
        this.iterationConsumer = iterationConsumer;
        return this;
    }

    /**
     * Sets threads and workers to default values for multi
     * threading tests.
     */
    public ProgressionConfigurator setDefaultMultiThreadedMode() {
        setConcurrencyLevel(32);
        return this;
    }

    /**
     * Sets the number of concurrent threads working on the test's
     * instance. It modifies both threads and workers accordingly.
     */
    public ProgressionConfigurator setConcurrencyLevel(
            final int concurrencyLevel) {
        setThreads(-1);
        setWorkers(concurrencyLevel);
        return this;
    }

    /**
     * How many threads should be created.
     * @see #setDefaultMultiThreadedMode()
     * @see #setConcurrencyLevel(int)
     */
    public ProgressionConfigurator setThreads(
            final int threads) {
        this.threads = threads;
        return this;
    }

    /** Creates as many threads as needed (matching workers). */
    public ProgressionConfigurator setUnlimitedThreads() {
        setThreads(-1);
        return this;
    }

    /**
     * Sets how many different tasks will compete for a thread.
     * 
     * @see #setDefaultMultiThreadedMode()
     * @see #setConcurrencyLevel(int)
     */
    public ProgressionConfigurator setWorkers(
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
    public ProgressionConfigurator setBaseIterations(
            final long baseIterations) {
        this.baseIterations = baseIterations;
        return this;
    }

    /**
     * Sets the maximum allowed standard deviation of the samples taken
     * in one progression.
     */
    public ProgressionConfigurator setMaxStandardDeviation(
            final double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
        return this;
    }

    /**
     * Sets the message that may be shown on the output viewers or
     * used in assertions.
     */
    public ProgressionConfigurator setMessage(
            final String message) {
        this.message = message;
        return this;
    }

    /** Prints the standard deviation on the standard output. */
    public ProgressionConfigurator setPrintOutStdDeviation(
            final boolean printOutStdDeviation) {
        this.printOutStdDeviation = printOutStdDeviation;
        return this;
    }

    /**
     * After how much time the test gives up with an exception.
     * Always use a sensible value because a performance test (even the
     * most obvious ones) can fail for a number of reasons or give strange
     * results that can make the calculations run forever. In this case
     * it's better to have some sort of time limitation.
     */
    public ProgressionConfigurator setTimeoutSeconds(
            final int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        return this;
    }
}
