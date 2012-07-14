package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.producer.AbstractPerformanceProducer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * The JVM actively optimizes the code at runtime based on the running
 * statistics it collects. This process takes place in multiple steps and may
 * be triggered by different events such as configurations or the number of
 * executions of a particular code.
 * That means that if you measure the performance on a small amount of iterations
 * you may not capture the performance of a full optimized code. To better
 * understand the point from which the performances stabilize this class
 * run several tests incrementing the iterations number in successive steps.
 *
 * @author fra
 */
public class ProgressionPerformanceInstrumenter
        extends AbstractPerformanceProducer<ProgressionPerformanceInstrumenter> {
    private static final long serialVersionUID = 1L;

    private final PerformanceTimer performanceTimer;
    private final long[] iterationsProgression;
    private final int samplePerMagnitude;
    private final long timeout;

    public static class Builder
            extends AbstractSequenceBuilder<ProgressionPerformanceInstrumenter>
            implements Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public ProgressionPerformanceInstrumenter build() {
            return new ProgressionPerformanceInstrumenter(this);
        }

    }

    public ProgressionPerformanceInstrumenter(
            final AbstractSequenceBuilder<?> builder) {
        this(builder.getPerformanceTimer(),
                builder.getIterationsProgression(),
                builder.getSamples(),
                builder.getTimeout());
    }

    public ProgressionPerformanceInstrumenter(
            final PerformanceTimer performanceTimer,
            final long[] iterationsProgression,
            final int samplePerMagnitude,
            final long timeout) {
        this.performanceTimer = performanceTimer;
        this.iterationsProgression = iterationsProgression;
        this.samplePerMagnitude = samplePerMagnitude;
        this.timeout = timeout;

        assert performanceTimer != null;
        assert iterationsProgression != null && iterationsProgression.length > 0;
        assert samplePerMagnitude > 0;
        assert timeout > 0;
    }

    /** Override if you need to stop the sequence. */
    protected boolean stopIterating(final SequencePerformances serie) {
        return false;
    }

    public LoopPerformances executeSequence() {
        long start = System.nanoTime();
        SequencePerformances sequencePerformances = null;
        for (long iterations: iterationsProgression) {
            for (int sample=0; sample<samplePerMagnitude; sample++) {
                final LoopPerformances loopPerformances =
                        performanceTimer.iterate((int)iterations);
                sequencePerformances = new SequencePerformances(loopPerformances);
                final String message = String.format("%,d", iterations);
                processConsumers(message, loopPerformances);
                if (System.nanoTime() - start > timeout) {
                    throw new RuntimeException("Timeout occurred.");
                }
            }
            if (stopIterating(sequencePerformances)) {
                break;
            }
        }
        return sequencePerformances.calculateAverageLoopPerformances();
    }
}
