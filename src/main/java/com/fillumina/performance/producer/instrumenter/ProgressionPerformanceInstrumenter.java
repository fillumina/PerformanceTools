package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.producer.AbstractPerformanceProducer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.LoopPerformancesHolder;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * The JVM actively optimizes the code at runtime based on the running
 * statistics it collects. This process takes place in multiple steps and may
 * be triggered by different events such as configurations or the number of
 * executions of a particular piece code.
 * That means that if you measure the performance on a small amount of iterations
 * you may not capture the performances of the full optimized code. To better
 * understand the point from which the performances stabilize this class
 * run several tests incrementing the iterations number in successive steps.
 *
 * @author fra
 */
public class ProgressionPerformanceInstrumenter
        extends AbstractPerformanceProducer<ProgressionPerformanceInstrumenter>
        implements Serializable,
            PerformanceInstrumenter<ProgressionPerformanceInstrumenter> {
    private static final long serialVersionUID = 1L;

    private final PerformanceTimer performanceTimer;
    private final long[] iterationsProgression;
    private final int samplePerMagnitude;
    private final long timeout;

    public static class Builder
            extends AbstractIstrumenterBuilder<Builder, ProgressionPerformanceInstrumenter>
            implements Serializable {
        private static final long serialVersionUID = 1L;

        public Builder() {
            super();
            
            // init with some defaults
            setIterationProgression(1000, 10_000L, 100_000L, 1_000_000L);
            setSamplePerIterations(10);
            setTimeout(5, TimeUnit.SECONDS);
        }

        @Override
        public ProgressionPerformanceInstrumenter build() {
            check();
            return new ProgressionPerformanceInstrumenter(this);
        }

    }

    public ProgressionPerformanceInstrumenter(
            final AbstractIstrumenterBuilder<?,?> builder) {
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

    @Override
    public LoopPerformancesHolder executeSequence() {
        long start = System.nanoTime();
        SequencePerformances sequencePerformances = null;
        for (long iterations: iterationsProgression) {
            sequencePerformances = new SequencePerformances();
            for (int sample=0; sample<samplePerMagnitude; sample++) {
                final LoopPerformances loopPerformances = performanceTimer
                        .iterate((int)iterations)
                        .getLoopPerformances();
                sequencePerformances.addLoopPerformances(loopPerformances);
                if (System.nanoTime() - start > timeout) {
                    throw new RuntimeException("Timeout occurred.");
                }
            }
            if (stopIterating(sequencePerformances)) {
                break;
            }
        }
        final LoopPerformances avgLoopPerformances =
                sequencePerformances.calculateAverageLoopPerformances();
        final String message = String.format("%,d",
                iterationsProgression[iterationsProgression.length - 1]);
        processConsumers(message, avgLoopPerformances);
        return new LoopPerformancesHolder(avgLoopPerformances);
    }
}
