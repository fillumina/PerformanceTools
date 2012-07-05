package com.fillumina.performance.producer.sequence;

import com.fillumina.performance.producer.AbstractChainablePerformanceProducer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class ProgressionSequence
        extends AbstractChainablePerformanceProducer<ProgressionSequence> {
    private static final long serialVersionUID = 1L;

    private final SequencePerformances sequence = new SequencePerformances();
    private final PerformanceTimer pt;

    private PerformanceConsumer iterationConsumer;
    private long timeout = TimeUnit.NANOSECONDS.convert(5, TimeUnit.SECONDS);

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

    /** Override if you need to stop the sequence. */
    protected boolean stopIterating(final SequencePerformances serie) {
        return false;
    }

    public ProgressionSequence sequence(final int baseTimes,
            final int maximumMagnitude,
            final int samplePerMagnitude) {
        long start = System.nanoTime();
        int index = 0;
        for (int magnitude=0; magnitude< maximumMagnitude; magnitude++) {
            for (int iteration=0; iteration<samplePerMagnitude; iteration++) {
                final long loops = calculateLoops(baseTimes, magnitude);
                pt.iterate((int)loops);
                pt.use(sequence);
                iterationConsumer();
                pt.clear();
                index++;
                if (System.nanoTime() - start > timeout) {
                    throw new RuntimeException("Timeout occurred.");
                }
            }
            if (stopIterating(sequence)) {
                break;
            }
            if (magnitude != maximumMagnitude - 1) {
                sequence.clear();
            }
        }
        processConsumer(String.valueOf(calculateLoops(baseTimes, maximumMagnitude)),
                sequence.getAverageLoopPerformances());
        return this;
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
