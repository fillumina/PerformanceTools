package com.fillumina.performance.util.junit;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.progression.StandardDeviationConsumer;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class PerformanceInstrumenterBuilder {
    private long baseIterations = 1_000;
    private double maxStandardDeviation = 10;
    private String message = "";
    private boolean printOutStdDeviation;
    private int timeoutSeconds = 10;
    private int threads = 1;
    private int workers = 1;

    /**
     * Override to return a {@link PerformanceExecutorInstrumenter}
     * other than {@link AutoProgressionPerformanceInstrumenter}.
     * @return null if no instrumenter has to be used.
     */
    protected InstrumentablePerformanceExecutor<?> createPerformanceExecutor() {
        final PerformanceTimer pe = createPerformanceTimer();

        return AutoProgressionPerformanceInstrumenter.builder()
                .instrument(pe)
                .setBaseIterations(baseIterations)
                .setSamplesPerMagnitude(10)
                .setMaxStandardDeviation(maxStandardDeviation)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setMessage(message)
                .build()
                .addStandardDeviationConsumer(new StandardDeviationConsumer() {

            @Override
            public void consume(final long iterations,
                    final long samples, final double stdDev) {
                    System.out.println(new StringBuilder()
                            .append("Iterations: ").append(iterations)
                            .append("\tSamples: ").append(samples)
                            .append("\tStandard Deviation: ").append(stdDev)
                            .toString());
            }
        });
    }

    private PerformanceTimer createPerformanceTimer() {
        if (threads == 1) {
            return PerformanceTimerBuilder.createSingleThread();
        }
        return PerformanceTimerBuilder.createMultiThread()
                .setThreads(threads)
                .setWorkers(workers)
                // timeout is managed in the instrumenter
                .setTimeout(100_000, TimeUnit.SECONDS)
                .build();
    }

    public PerformanceInstrumenterBuilder setThreads(
            final int threads) {
        this.threads = threads;
        return this;
    }

    public PerformanceInstrumenterBuilder setWorkers(
            final int workers) {
        this.workers = workers;
        return this;
    }

    public PerformanceInstrumenterBuilder setBaseIterations(
            final long baseIterations) {
        this.baseIterations = baseIterations;
        return this;
    }

    public PerformanceInstrumenterBuilder setMaxStandardDeviation(
            final double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
        return this;
    }

    public PerformanceInstrumenterBuilder setMessage(
            final String message) {
        this.message = message;
        return this;
    }

    public PerformanceInstrumenterBuilder setPrintOutStdDeviation(
            final boolean printOutStdDeviation) {
        this.printOutStdDeviation = printOutStdDeviation;
        return this;
    }

    public PerformanceInstrumenterBuilder setTimeoutSeconds(
            final int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        return this;
    }
}
