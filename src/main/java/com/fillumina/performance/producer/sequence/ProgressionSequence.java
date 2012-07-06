package com.fillumina.performance.producer.sequence;

import com.fillumina.performance.producer.BaseFluentPerformanceProducer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.FluentPerformanceProducer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.TimeUnit;

/**
 * The JVM actively optimizes the code at runtime based on the running
 * statistics it collects. This process takes place in multiple steps and is
 * performed over the most critical parts of the application.
 * That means that if you measure the performance on a small amount of iterations
 * you may not capture the performance of a full optimized code. To better
 * understand the point from which the performances stabilize this class
 * run several tests incrementing the iterations number in successive steps.
 *
 *
 * @author fra
 */
public class ProgressionSequence
        extends BaseFluentPerformanceProducer<ProgressionSequence> {
    private static final long serialVersionUID = 1L;

    private final SequencePerformances sequence = new SequencePerformances();
    private final PerformanceTimer pt;

    private PerformanceConsumer iterationConsumer;
    private long timeout = TimeUnit.NANOSECONDS.convert(5, TimeUnit.SECONDS);

    private long[] iterationsProgression;
    private int samplePerMagnitude = 10;

    public ProgressionSequence(final PerformanceTimer pt) {
        this.pt = pt;
    }

    /**
     * The assigned consumer receives all the intermediate
     * test sequence results.
     */
    public ProgressionSequence setOnIterationPerformanceConsumer(
            PerformanceConsumer consumer) {
        this.iterationConsumer = consumer;
        return this;
    }

    public ProgressionSequence setTimeout(final int time, final TimeUnit unit) {
        timeout = unit.toNanos(time);
        return this;
    }

    public ProgressionSequence setBaseAndMagnitude(
            final int baseTimes, final int maximumMagnitude) {
        iterationsProgression = new long[maximumMagnitude];
        for (int magnitude = 0; magnitude<maximumMagnitude; magnitude++) {
            iterationsProgression[magnitude] = calculateLoops(baseTimes, magnitude);
        }
        return this;
    }

    public ProgressionSequence setIterationsProgression(final long... iterations) {
        this.iterationsProgression = iterations;
        return this;
    }

    public ProgressionSequence setSamplePerIterations(final int samplePerMagnitude) {
        this.samplePerMagnitude = samplePerMagnitude;
        return this;
    }

    /** Override if you need to stop the sequence. */
    protected boolean stopIterating(final SequencePerformances serie) {
        return false;
    }

    public FluentPerformanceProducer executeSequence() {
        long start = System.nanoTime();
        long lastIterations = 0;
        for (long iterations: iterationsProgression) {
            lastIterations = iterations;
            sequence.clear();
            for (int sample=0; sample<samplePerMagnitude; sample++) {
                pt.iterate((int)iterations);
                pt.use(sequence);
                iterationConsumer();
                pt.clear();
                if (System.nanoTime() - start > timeout) {
                    throw new RuntimeException("Timeout occurred.");
                }
            }
            if (stopIterating(sequence)) {
                break;
            }
        }
        final LoopPerformances averageLoopPerformances =
                sequence.getAverageLoopPerformances();

        processConsumer(String.valueOf(lastIterations), averageLoopPerformances);

        return new BaseFluentPerformanceProducer<>(averageLoopPerformances);
    }

    private long calculateLoops(final int baseTimes, int magnitude) {
        return Math.round(baseTimes * Math.pow(10, magnitude));
    }

    private void iterationConsumer() {
        if (iterationConsumer != null) {
            pt.use(iterationConsumer).process();
        }
    }

    public SequencePerformances getSeriePerformance() {
        return sequence;
    }
}
