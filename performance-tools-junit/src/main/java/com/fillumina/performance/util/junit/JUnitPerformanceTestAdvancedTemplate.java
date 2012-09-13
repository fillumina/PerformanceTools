package com.fillumina.performance.util.junit;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.SingleThreadPerformanceTestExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public abstract class JUnitPerformanceTestAdvancedTemplate
        extends JUnitPerformanceTestSimpleTemplate {

    @Override
    public void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        final InstrumentablePerformanceExecutor<?> pe =
                createPerformanceExecutor(iterationConsumer, resultConsumer);

        addTests(pe);

        pe.execute().use(createAssertions());
    }

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
                .setBaseIterations(getBaseIterations())
                .setSamplesPerMagnitude(10)
                .setMaxStandardDeviation(getMaxStandardDeviation())
                .setTimeout(getTimeoutSeconds(), TimeUnit.SECONDS)
                .build();
    }

    /** Override to set the base number of iterations. */
    protected int getBaseIterations() {
        return 10;
    }

    /** Override to set the desired maximum standard deviation. */
    protected int getMaxStandardDeviation() {
        return 10;
    }

    /** Override to set a timeout in seconds. */
    protected int getTimeoutSeconds() {
        return 10;
    }
}
