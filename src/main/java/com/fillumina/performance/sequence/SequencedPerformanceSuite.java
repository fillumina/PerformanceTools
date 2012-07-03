package com.fillumina.performance.sequence;

import com.fillumina.performance.InitializingRunnable;
import com.fillumina.performance.PerformanceConsumer;
import com.fillumina.performance.PerformanceProducer;
import com.fillumina.performance.timer.PerformanceTimer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author fra
 */
public class SequencedPerformanceSuite<P,S>
        implements PerformanceProducer, Serializable {
    private static final long serialVersionUID = 1L;

    private final PerformanceTimer performanceTimer;
    private final List<SequencedInnerRunnable> runnables = new ArrayList<>();
    private SequencedParametrizedRunnable<P,S> callable;
    private PerformanceConsumer consumer;
    private Iterable<S> sequence;

    public SequencedPerformanceSuite(final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
    }

    public SequencedPerformanceSuite<P,S> addObjectToTest(final String message,
            final P t) {
        final SequencedInnerRunnable sir = new SequencedInnerRunnable(t);
        runnables.add(sir);
        performanceTimer.addTest(message, sir);
        return this;
    }

    public SequencedPerformanceSuite<P,S> setSequence(
            final Iterable<S> iterable) {
        this.sequence = iterable;
        return this;
    }

    public SequencedPerformanceSuite<P,S> setSequence(
            final Iterator<S> sequence) {
        this.sequence = new Iterable<S>() {

            @Override
            public Iterator<S> iterator() {
                return sequence;
            }

        };
        return this;
    }

    @Override
    public SequencedPerformanceSuite<P,S> setPerformanceConsumer(
            final PerformanceConsumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public PerformanceTimer execute(final int loops,
            final SequencedParametrizedRunnable<P,S> test) {
        setTest(test);
        for (S sequenceItem: sequence) {
            for (SequencedInnerRunnable sir: runnables) {
                sir.setSequenceItem(sequenceItem);
            }
            performanceTimer.clear();
            performanceTimer.iterate(loops);
            if (consumer != null) {
                performanceTimer.use(consumer)
                    .setMessage(sequenceItem.toString())
                    .process();
            }
        }
        return performanceTimer;
    }

    private void setTest(final SequencedParametrizedRunnable<P,S> callable) {
        this.callable = callable;
    }

    @Override
    public <T extends PerformanceConsumer> T use(T consumer) {
        this.consumer = consumer;
        return consumer;
    }

    public class SequencedInnerRunnable implements InitializingRunnable {
        private final P param;
        private S sequenceItem;

        private SequencedInnerRunnable(final P param) {
            this.param = param;
        }

        private void setSequenceItem(final S sequenceItem) {
            this.sequenceItem = sequenceItem;
        }

        @Override
        public void init() {
            callable.setUp(param, sequenceItem);
        }

        @Override
        public void run() {
            callable.call(param, sequenceItem);
        }
    }
}
