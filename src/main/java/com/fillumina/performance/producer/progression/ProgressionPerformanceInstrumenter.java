package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.LoopPerformancesSequence;
import com.fillumina.performance.producer.AbstractInstrumentablePerformanceProducer;
import com.fillumina.performance.producer.Instrumentable;
import com.fillumina.performance.producer.PerformanceExecutor;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.AbstractPerformanceTimer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.util.TimeUnitHelper;
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
        extends AbstractInstrumentablePerformanceProducer<ProgressionPerformanceInstrumenter>
        implements Serializable,
            PerformanceExecutor<ProgressionPerformanceInstrumenter>,
            Instrumentable {
    private static final long serialVersionUID = 1L;

    private final PerformanceExecutor<?> performanceExecutor;
    private final int[] iterationsProgression;
    private final int samplesPerMagnitude;
    private final long timeout;

    public static ProgressionPerformanceInstrumenterBuilder builder() {
        return new ProgressionPerformanceInstrumenterBuilder();
    }

    public ProgressionPerformanceInstrumenter(
            final ProgressionPerformanceInstrumenterBuilder builder) {
        this(builder.getPerformanceExecutor(),
                builder.getIterationsProgression(),
                builder.getSamplesPerMagnitude(),
                builder.getTimeoutInNanoseconds());
    }

    public ProgressionPerformanceInstrumenter(
            final PerformanceExecutor<?> performanceExecutor,
            final int[] iterationsProgression,
            final int samplesPerMagnitude,
            final long timeout) {
        assert performanceExecutor != null;
        assert iterationsProgression != null && iterationsProgression.length > 0;
        assert samplesPerMagnitude > 0;
        assert timeout > 0;

        this.performanceExecutor = performanceExecutor;
        this.iterationsProgression = iterationsProgression;
        this.samplesPerMagnitude = samplesPerMagnitude;
        this.timeout = timeout;
    }

    /** Override if you need to stop the sequence. */
    protected boolean stopIterating(final LoopPerformancesSequence serie) {
        return false;
    }

    @Override
    public <T extends PerformanceExecutorInstrumenter> T
            instrumentedBy(final T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    @Override
    public ProgressionPerformanceInstrumenter addTest(
            final String name,
            final Runnable test) {
        performanceExecutor.addTest(name, test);
        return this;
    }

    @Override
    public LoopPerformancesHolder execute() {
        long start = System.nanoTime();
        LoopPerformancesSequence sequencePerformances = null;

        for (int iterations: iterationsProgression) {
            sequencePerformances = new LoopPerformancesSequence();

            for (int sample=0; sample<samplesPerMagnitude; sample++) {
                setIterationsIfNeeded(iterations);
                final LoopPerformances loopPerformances = performanceExecutor
                        .execute()
                        .getLoopPerformances();

                sequencePerformances.addLoopPerformances(loopPerformances);

                checkForTimeout(start);
            }

            if (stopIterating(sequencePerformances)) {
                break;
            }
        }

        final LoopPerformances avgLoopPerformances =
                sequencePerformances.calculateAverageLoopPerformances();

        processConsumers(avgLoopPerformances);

        return new LoopPerformancesHolder(avgLoopPerformances);
    }

    private void checkForTimeout(long start) {
        if (System.nanoTime() - start > timeout) {
            throw new RuntimeException("Timeout occurred: test was lasting " +
                    "more than required maximum of " +
                    TimeUnitHelper.prettyPrint(timeout, TimeUnit.NANOSECONDS));
        }
    }

    private void processConsumers(final LoopPerformances avgLoopPerformances) {
        final String message = String.format("%,d",
                iterationsProgression[iterationsProgression.length - 1]);
        processConsumers(message, avgLoopPerformances);
    }

    public void setIterationsIfNeeded(final int iterations) {
        if (performanceExecutor instanceof AbstractPerformanceTimer) {
            ((AbstractPerformanceTimer<?>)performanceExecutor)
                    .setIterations(iterations);
        }
    }
}
