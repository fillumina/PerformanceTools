package com.fillumina.performance.producer.suite;

import com.fillumina.performance.executor.Testable;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.LoopPerformancesSequence;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 * @author Francesco Illuminati
 */
public class ParametrizedSequencePerformanceSuite<P,S>
        extends AbstractParametrizedInstrumenterSuite
                <ParametrizedSequencePerformanceSuite<P,S>, P>
        implements PerformanceExecutorInstrumenter,
            SequenceContainer<ParametrizedSequencePerformanceSuite<P,S>, S>,
            ParametrizedSequenceExecutor<P, S> {

    private static final long serialVersionUID = 1L;

    private final List<SequenceParameterTestable> tests = new ArrayList<>();
    private final Map<String, S> sequence = new LinkedHashMap<>();

    private ParametrizedSequenceTestable<P,S> actualTest;

    @Override
    @SuppressWarnings("unchecked")
    protected Testable createTest(final Object param) {
        final SequenceParameterTestable omr =
                new SequenceParameterTestable((P)param);
        tests.add(omr);
        return omr;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P, S> setSequence(
            Map<String, S> namedSequence) {
        sequence.putAll(namedSequence);
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P, S> setSequenceItem(
            String name, S item) {
        sequence.put(name, item);
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S> setSequence(
            final S... sequence) {
        for (S s : sequence) {
            this.sequence.put(s.toString(), s);
        }
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S> setSequence(
            final Iterable<S> iterable) {
        for (S s : iterable) {
            this.sequence.put(s.toString(), s);
        }
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
            final ParametrizedSequenceTestable<P,S> test) {
        return executeTest(null, test);
    }

    @Override
    public LoopPerformancesHolder ignoreTest(
            final ParametrizedSequenceTestable<P,S> test) {
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
            final ParametrizedSequenceTestable<P,S> test) {
        addTestsToPerformanceExecutor();
        this.actualTest = test;
        final LoopPerformancesSequence.Running lpSeq =
                new LoopPerformancesSequence.Running();

        for (final Map.Entry<String,S> entry : sequence.entrySet()) {
            final String itemName = entry.getKey();
            final S sequenceItem = entry.getValue();
            final String composedName = createName(name, itemName);

            for (SequenceParameterTestable t: tests) {
                t.setSequenceItem(sequenceItem);
            }

            final LoopPerformances loopPerformances =
                    getPerformanceExecutor().execute().getLoopPerformances();

            dispatchPerformanceToConsumers(composedName, loopPerformances);
            addTestLoopPerformances(composedName, loopPerformances);

            lpSeq.addLoopPerformances(loopPerformances);
        }

        return new LoopPerformancesHolder(name,
                lpSeq.calculateAverageLoopPerformances());
    }

    @Override
    public LoopPerformancesHolder ignoreTest(final String name,
            final ParametrizedSequenceTestable<P,S> test) {
        return LoopPerformancesHolder.empty();
    }

    public static String createName(final String paramName,
            final String sequenceName) {
        if (sequenceName == null && paramName == null) {
            return null;
        }
        if (sequenceName == null) {
            return paramName;
        }
        if (paramName == null) {
            return sequenceName;
        }
        return paramName + "-" + sequenceName;
    }

    private class SequenceParameterTestable implements Testable {
        private final P param;
        private S sequenceItem;

        private SequenceParameterTestable(final P param) {
            this.param = param;
        }

        private void setSequenceItem(final S sequenceItem) {
            this.sequenceItem = sequenceItem;
        }

        @Override
        public void setUp() {
            actualTest.setUp(param, sequenceItem);
        }

        @Override
        public void onBeforeSample(int iterations) {
            actualTest.beforeTest(param, sequenceItem, iterations);
        }

        @Override
        public Object test() {
            return actualTest.test(param, sequenceItem);
        }
    }
}
