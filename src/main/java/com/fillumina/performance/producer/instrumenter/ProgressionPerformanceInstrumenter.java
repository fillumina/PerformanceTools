package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.producer.timer.PerformanceProducerInstrumenter;
import com.fillumina.performance.producer.BaseFluentPerformanceProducer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.FluentPerformanceProducer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.PerformanceTimer;
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
 * If you set a performance consumer with
 * {@link #setOnIterationPerformanceConsumer(com.fillumina.performance.consumer.PerformanceConsumer) }
 * you can see how the performances change over time.
 *
 *
 * @author fra
 */
public class ProgressionPerformanceInstrumenter
        extends BaseFluentPerformanceProducer<ProgressionPerformanceInstrumenter>
        implements PerformanceProducerInstrumenter<ProgressionPerformanceInstrumenter> {
    private static final long serialVersionUID = 1L;

    private final SequencePerformances sequence = new SequencePerformances();

    private PerformanceTimer pt;
    private long timeout = TimeUnit.NANOSECONDS.convert(5, TimeUnit.SECONDS);
    private long[] iterationsProgression;
    private int samplePerMagnitude = 10;
    private PerformanceConsumer innerPerformanceConsumer;

    @Override
    public void setPerformanceTimer(final PerformanceTimer performanceTimer) {
        this.pt = performanceTimer;
    }

    public ProgressionPerformanceInstrumenter setTimeout(final int time, final TimeUnit unit) {
        timeout = unit.toNanos(time);
        return this;
    }

    /**
     * This is alternative to
     * {@link #setIterations(long[]) }.
     */
    public ProgressionPerformanceInstrumenter setBaseAndMagnitude(
            final int baseTimes, final int maximumMagnitude) {
        iterationsProgression = new long[maximumMagnitude];
        for (int magnitude = 0; magnitude<maximumMagnitude; magnitude++) {
            iterationsProgression[magnitude] = calculateLoops(baseTimes, magnitude);
        }
        return this;
    }

    /**
     * This is alternative to
     * {@link ProgressionSequence#setBaseAndMagnitude(int, int) }.
     */
    public ProgressionPerformanceInstrumenter setIterations(final long... iterations) {
        this.iterationsProgression = iterations;
        return this;
    }

    public ProgressionPerformanceInstrumenter setSamplePerIterations(final int samplePerMagnitude) {
        this.samplePerMagnitude = samplePerMagnitude;
        return this;
    }

    /** Override if you need to stop the sequence. */
    protected boolean stopIterating(final SequencePerformances serie) {
        return false;
    }

    public FluentPerformanceProducer executeSequence() {
        assertPerformanceTimerAssigned();
        long start = System.nanoTime();
        long lastIterations = 0;
        for (long iterations: iterationsProgression) {
            lastIterations = iterations;
            sequence.clear();
            for (int sample=0; sample<samplePerMagnitude; sample++) {
                pt.iterate((int)iterations);
                pt.use(sequence);
                callInnerPerformanceConsumer();
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

    private void callInnerPerformanceConsumer() {
        if (innerPerformanceConsumer != null) {
            innerPerformanceConsumer
                    .setPerformances(pt.getLoopPerformances())
                    .process();
        }
    }

    private long calculateLoops(final int baseTimes, int magnitude) {
        return Math.round(baseTimes * Math.pow(10, magnitude));
    }

    public SequencePerformances getSeriePerformance() {
        return sequence;
    }

    private void assertPerformanceTimerAssigned() {
        if (pt == null) {
            throw new RuntimeException("PerformanceTimer should be assigned");
        }
    }

    @Override
    public ProgressionPerformanceInstrumenter setInnerPerformanceConsumer(
            final PerformanceConsumer consumer) {
        this.innerPerformanceConsumer = consumer;
        return this;
    }
}
