package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.AbstractPerformanceProducer;
import com.fillumina.performance.producer.timer.InitializableRunnable;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author fra
 */
public class ParametrizedSequencePerformanceSuite<P,S>
        extends AbstractPerformanceProducer<ParametrizedSequencePerformanceSuite<P,S>> {
    private static final long serialVersionUID = 1L;

    private final PerformanceTimer performanceTimer;
    private final List<ObjectMatrixInnerRunnable> tests = new ArrayList<>();
    private ParametrizedSequenceRunnable<P,S> callable;
    private Iterable<S> sequence;

    public ParametrizedSequencePerformanceSuite(
            final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
    }

    public ParametrizedSequencePerformanceSuite<P,S> addObjectToTest(
            final String message, final P t) {
        final ObjectMatrixInnerRunnable sir = new ObjectMatrixInnerRunnable(t);
        tests.add(sir);
        performanceTimer.addTest(message, sir);
        return this;
    }

    public ParametrizedSequencePerformanceSuite<P,S> addSequence(
            final S... sequence) {
        this.sequence = Arrays.asList(sequence);
        return this;
    }

    public ParametrizedSequencePerformanceSuite<P,S> addSequence(
            final Iterable<S> iterable) {
        this.sequence = iterable;
        return this;
    }

    public ParametrizedSequencePerformanceSuite<P,S> addSequence(
            final Iterator<S> iterator) {
        this.sequence = new Iterable<S>() {

            @Override
            public Iterator<S> iterator() {
                return iterator;
            }

        };
        return this;
    }

    public PerformanceTimer executeTest(final int iterationNumber,
            final ParametrizedSequenceRunnable<P,S> test) {
        setActualTest(test);
        for (final S sequenceItem: sequence) {
            for (ObjectMatrixInnerRunnable sir: tests) {
                sir.setSequenceItem(sequenceItem);
            }
            final LoopPerformances loopPerformances =
                    performanceTimer.iterate(iterationNumber).getLoopPerformances();
            processConsumers(sequenceItem.toString(), loopPerformances);
        }
        return performanceTimer;
    }

    private void setActualTest(final ParametrizedSequenceRunnable<P,S> callable) {
        this.callable = callable;
    }

    private class ObjectMatrixInnerRunnable implements InitializableRunnable {
        private final P param;
        private S sequenceItem;

        private ObjectMatrixInnerRunnable(final P param) {
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
