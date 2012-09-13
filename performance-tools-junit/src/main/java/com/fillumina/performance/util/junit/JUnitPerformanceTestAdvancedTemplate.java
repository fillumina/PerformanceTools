package com.fillumina.performance.util.junit;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.progression.StandardDeviationConsumer;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.SingleThreadPerformanceTestExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public abstract class JUnitPerformanceTestAdvancedTemplate
        extends JUnitPerformanceTestSimpleTemplate {

    private long baseIterations = 1_000;
    private double maxStandardDeviation = 10;
    private int timeoutSeconds = 10;
    private String message = "";
    private boolean printOutStdDeviation;

    @Override
    public void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {
        init();

        final InstrumentablePerformanceExecutor<?> pe =
                createPerformanceExecutor(iterationConsumer, resultConsumer);

        addTests(pe);

        final LoopPerformancesHolder lph = pe.execute();
        final PerformanceConsumer assertion = createAssertions();
        if (assertion != null) {
            lph.use(createAssertions());
        }
    }

    protected abstract void init();

    protected abstract void addTests(final InstrumentablePerformanceExecutor<?> pe);

    protected abstract PerformanceConsumer createAssertions();

    /**
     * Override to provide a
     * {@link InstrumentablePerformanceExecutor}.
     */
    protected InstrumentablePerformanceExecutor<?> createPerformanceExecutor(
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        InstrumentablePerformanceExecutor<?> pe = createPerformanceTimer();

        InstrumentablePerformanceExecutor<?> instrumenter =
                    createPerformanceInstrumenter(pe);

        if (instrumenter != null) {
            pe.addPerformanceConsumer(iterationConsumer);
            instrumenter.addPerformanceConsumer(resultConsumer);
            return instrumenter;

        } else {
            pe.addPerformanceConsumer(resultConsumer);
            return pe;

        }
    }

    /**
     * Override to return a {@link PerformanceTimer} with an executor
     * other than {@link SingleThreadPerformanceTestExecutor}.
     */
    protected PerformanceTimer createPerformanceTimer() {
        return PerformanceTimerBuilder.createSingleThread();
    }

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

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBaseIterations(long baseIterations) {
        this.baseIterations = baseIterations;
    }

    public void setMaxStandardDeviation(double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public void setPrintOutStdDeviation(boolean printOutStdDeviation) {
        this.printOutStdDeviation = printOutStdDeviation;
    }
}
