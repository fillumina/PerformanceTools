package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.InitializingRunnable;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.LoopPerformancesSequence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Instrumenter that allows to add a sequence to a suite (test with a parameter)
 * so that it can be checked against different values.
 * I.e. it can be used to test
 * the performances of different type of maps with different sizes.
 * <p>
 * Performance are produced at each item of the sequence. The returned
 * performances (by the {@code executeTest()} method) are the average
 * of all the items in the sequence.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedSequencePerformanceSuite<P,S>
        extends AbstractParametrizedInstrumenterSuite
                <ParametrizedSequencePerformanceSuite<P,S>, P>
        implements PerformanceExecutorInstrumenter,
            SequenceContainer<ParametrizedSequencePerformanceSuite<P,S>, S>,
            ParametrizedSequenceExecutor<P, S> {

    private static final long serialVersionUID = 1L;

    private final List<ParameterMatrixInnerRunnable> tests = new ArrayList<>();
    private ParametrizedSequenceRunnable<P,S> actualTest;
    private Iterable<S> sequence;

    @SuppressWarnings("unchecked")
    private SequenceNominator<S> sequenceNominator =
            (SequenceNominator<S>) SequenceNominator.DEFAULT;

    @Override
    @SuppressWarnings("unchecked")
    protected Runnable wrap(final Object param) {
        final ParameterMatrixInnerRunnable omr =
                new ParameterMatrixInnerRunnable((P)param);
        tests.add(omr);
        return omr;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S> setSequence(
            final S... sequence) {
        this.sequence = Arrays.asList(sequence);
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S> setSequence(
            final Iterable<S> iterable) {
        this.sequence = iterable;
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S> setSequence(
            final Iterator<S> iterator) {
        this.sequence = new Iterable<S>() {

            @Override
            public Iterator<S> iterator() {
                return iterator;
            }

        };
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S> setSequenceNominator(
            final SequenceNominator<S> sequenceNominator) {
        this.sequenceNominator = sequenceNominator;
        return this;
    }

    /**
     * Executes the given test against the previously added parameters and
     * sequence. The outer loop is the sequence so the consumers will be
     * called at each element for the sequence.
     *
     * @return the performances by parameter averaged for the elements of the
     *          sequence.
     */
    @Override
    public LoopPerformancesHolder executeTest(
            final ParametrizedSequenceRunnable<P,S> test) {
        return executeTest(null, test);
    }

    @Override
    public LoopPerformancesHolder ignoreTest(
            final ParametrizedSequenceRunnable<P,S> test) {
        return LoopPerformancesHolder.empty();
    }

    /**
     * Executes the given named test against the previously added parameters and
     * sequence. The outer loop is the sequence.
     *
     * @return the performances by parameter averaged for the elements of the
     *          sequence.
     */
    @Override
    public LoopPerformancesHolder executeTest(final String name,
            final ParametrizedSequenceRunnable<P,S> test) {
        addTestsToPerformanceExecutor();
        this.actualTest = test;
        final LoopPerformancesSequence.Running lpSeq =
                new LoopPerformancesSequence.Running();

        for (final S sequenceItem: sequence) {
            for (ParameterMatrixInnerRunnable pmir: tests) {
                pmir.setSequenceItem(sequenceItem);
            }

            final LoopPerformances loopPerformances =
                    getPerformanceExecutor().execute().getLoopPerformances();

            final String composedName =
                    createName(name, sequenceNominator.toString(sequenceItem));

            consume(composedName, loopPerformances);
            addTestLoopPerformances(composedName, loopPerformances);

            lpSeq.addLoopPerformances(loopPerformances);
        }

        return new LoopPerformancesHolder(name,
                lpSeq.calculateAverageLoopPerformances());
    }

    @Override
    public LoopPerformancesHolder ignoreTest(final String name,
            final ParametrizedSequenceRunnable<P,S> test) {
        return LoopPerformancesHolder.empty();
    }

    public static String createName(final String paramName, final Object seq) {
        if (seq == null && paramName == null) {
            return null;
        }
        if (seq == null) {
            return paramName;
        }
        if (paramName == null) {
            return seq.toString();
        }
        return paramName + "-" + seq.toString();
    }

    private class ParameterMatrixInnerRunnable implements InitializingRunnable {
        private final P param;
        private S sequenceItem;

        private ParameterMatrixInnerRunnable(final P param) {
            this.param = param;
        }

        private void setSequenceItem(final S sequenceItem) {
            this.sequenceItem = sequenceItem;
        }

        @Override
        public void init() {
            actualTest.setUp(param, sequenceItem);
        }

        @Override
        public void run() {
            actualTest.call(param, sequenceItem);
        }
    }
}
