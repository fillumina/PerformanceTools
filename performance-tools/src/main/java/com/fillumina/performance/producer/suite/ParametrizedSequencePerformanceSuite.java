package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.InitializableRunnable;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.LoopPerformancesSequence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedSequencePerformanceSuite<P,S>
        extends AbstractParametrizedInstrumenterSuite
                <ParametrizedSequencePerformanceSuite<P,S>, P>
        implements PerformanceExecutorInstrumenter,
            SequencesContainer<ParametrizedSequencePerformanceSuite<P,S>, S> {

    private static final long serialVersionUID = 1L;

    private final List<ObjectMatrixInnerRunnable> tests = new ArrayList<>();
    private ParametrizedSequenceRunnable<P,S> callable;
    private Iterable<S> sequence;

    @Override
    @SuppressWarnings("unchecked")
    protected Runnable wrap(final Object object) {
        final ObjectMatrixInnerRunnable omr =
                new ObjectMatrixInnerRunnable((P)object);
        tests.add(omr);
        return omr;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S> addSequence(
            final S... sequence) {
        this.sequence = Arrays.asList(sequence);
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S> addSequence(
            final Iterable<S> iterable) {
        this.sequence = iterable;
        return this;
    }

    @Override
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

    public LoopPerformancesHolder executeTest(
            final ParametrizedSequenceRunnable<P,S> test) {
        return executeTest(null, test);
    }

    public LoopPerformancesHolder executeTest(final String name,
            final ParametrizedSequenceRunnable<P,S> test) {
        addTestsToPerformanceExecutor();
        setActualTest(test);
        final LoopPerformancesSequence lpSeq = new LoopPerformancesSequence();
        for (final S sequenceItem: sequence) {
            for (ObjectMatrixInnerRunnable sir: tests) {
                sir.setSequenceItem(sequenceItem);
            }

            final LoopPerformances loopPerformances =
                    getPerformanceExecutor().execute().getLoopPerformances();

            final String composedName = createName(name, sequenceItem);
            consume(composedName, loopPerformances);
            addTestLoopPerformances(composedName, loopPerformances);

            lpSeq.addLoopPerformances(loopPerformances);
        }
        return new LoopPerformancesHolder(name,
                lpSeq.calculateAverageLoopPerformances());
    }

    private void setActualTest(final ParametrizedSequenceRunnable<P,S> callable) {
        this.callable = callable;
    }

    public static String createName(final Object obj, final Object seq) {
        return (obj == null ? "" : obj.toString() + "-") + seq.toString();
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
