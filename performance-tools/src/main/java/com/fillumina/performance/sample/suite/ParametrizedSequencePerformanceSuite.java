package com.fillumina.performance.sample.suite;

import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.Testable;
import java.util.LinkedHashMap;
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
                <ParametrizedSequencePerformanceSuite<P,S>,
                 ParametrizedSequenceTestable<P,S>,
                 P>
        implements
            SequenceContainer<ParametrizedSequencePerformanceSuite<P,S>, S> {

    private final Map<String, S> sequence = new LinkedHashMap<>();

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
    public ParametrizedSequencePerformanceSuite<P, S> setSequence(
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

    public static String createName(String... strs) {
        StringBuilder buf = new StringBuilder();
        for (int i=0, len = strs.length; i<len; i++) {
            String s = strs[i];
            if (s != null) {
                buf.append(s);
                if (i != len -1) {
                    buf.append('_');
                }
            }
        }
        return buf.toString();
    }

    @Override
    protected void createTests() {
        Map<String, ParametrizedSequenceTestable<P,S>> tests = getTests();
        if (!tests.isEmpty()) {
            DefaultPerformanceTimer pt = getPerformanceTimer();
            for (Map.Entry<String, ParametrizedSequenceTestable<P,S>> test :
                    tests.entrySet()) {
                String testName = test.getKey();
                ParametrizedSequenceTestable<P,S> testable = test.getValue();

                for (Map.Entry<String, S> seq : sequence.entrySet()) {
                    String seqName = seq.getKey();
                    S seqItem = seq.getValue();

                    for (Map.Entry<String, P> par : getParams().entrySet()) {
                        String paramName = par.getKey();
                        P param = par.getValue();

                        pt.addTest(createName(testName, seqName, paramName),
                                new ParametrizedSequenceTestableImpl<>(
                                        testable, seqItem, param));
                    }
                }
            }
            tests.clear();
            sequence.clear();
        }
    }

    private static class ParametrizedSequenceTestableImpl<P,S>
            implements Testable {
        private final ParametrizedSequenceTestable<P,S> test;
        private final S sequenceItem;
        private final P param;

        public ParametrizedSequenceTestableImpl(
                ParametrizedSequenceTestable<P, S> test, S seqItem, P param) {
            this.test = test;
            this.sequenceItem = seqItem;
            this.param = param;
        }

        @Override
        public void setUp() {
            test.setUp(param, sequenceItem);
        }

        @Override
        public void onBeforeSample(int iterations) {
            test.beforeTest(param, sequenceItem, iterations);
        }

        @Override
        public Object test() {
            return test.test(param, sequenceItem);
        }
    }
}
