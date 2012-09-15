package com.fillumina.performance.util.junit;

import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.progression.StandardDeviationConsumer;
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

    /**
     * Override to return a {@link PerformanceExecutorInstrumenter}
     * other than {@link AutoProgressionPerformanceInstrumenter}.
     * @return null if no instrumenter has to be used.
     */
    protected InstrumentablePerformanceExecutor<?> createPerformanceInstrumenter(
            final InstrumentablePerformanceExecutor<?> pe) {

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
            public void consume(final double stdDev) {
                if (printOutStdDeviation) {
                    System.out.println("Standard Deviation = " + stdDev);
                }
            }
        });
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
