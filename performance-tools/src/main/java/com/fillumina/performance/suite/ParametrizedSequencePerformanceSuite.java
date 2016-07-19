package com.fillumina.performance.suite;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Instrumenter that allows to append a sequence to a suite (test with a parameter)
 so that it can be checked against different values.
 * I.e. it can be used to test
 * the performances of different type of maps with different sizes.
 * <p>
 * Performance are produced at each item of the sequence. The returned
 * performances (by the {@code executeTest()} method) are the average
 * of all the items in the sequence.
 *
 * @author Francesco Illuminati
 */
public class ParametrizedSequencePerformanceSuite
                    <P,S,A extends AssertableMultiStats>
        extends AbstractPerformanceProducer
            <ParametrizedSequencePerformanceSuite<P,S,A>,
             A,
             Map<ComposedName, Map<ComposedName, A>>,
             ParametrizedSequenceTestable<P,S>>
        implements ParametrizedSequenceStatsProducer<P,S,A>,
            SequenceContainer<S>,
            Instrumenter<ParametrizedStatsProducer<P,A>> {

    private final Map<String, S> sequence = new LinkedHashMap<>();
    private final StringGenerator<Map<ComposedName, Map<ComposedName, A>>>
            stringGenerator;
    private ParametrizedStatsProducer<P,A> producer;

    public ParametrizedSequencePerformanceSuite(
            StringGenerator<Map<ComposedName, Map<ComposedName, A>>> stringGenerator) {
        this.stringGenerator = stringGenerator;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S,A> setSequence(
            Map<String, S> namedSequence) {
        sequence.putAll(namedSequence);
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S,A> setSequenceItem(
            String name, S item) {
        sequence.put(name, item);
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S,A> setSequence(
            final S... sequence) {
        for (S s : sequence) {
            this.sequence.put(s.toString(), s);
        }
        return this;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S,A> setSequence(
            final Iterable<S> iterable) {
        for (S s : iterable) {
            this.sequence.put(s.toString(), s);
        }
        return this;
    }

    @Override
    public <T extends Instrumenter<ParametrizedSequenceStatsProducer<P,S,A>>>
            T instrumentedBy(T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S,A> instrument(
            ParametrizedStatsProducer<P,A> instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    @Override
    public TreeHolder<A, Map<ComposedName, Map<ComposedName, A>>>
                execute() {
        Map<ComposedName,Map<ComposedName,A>> map = new LinkedHashMap<>();
        Map<String, ParametrizedSequenceTestable<P,S>> tests = getTests();
        if (tests.isEmpty()) {
            throw new IllegalStateException("no test found");
        }
        if (sequence.isEmpty()) {
            throw new IllegalStateException("no sequence found");
        }
        if (!tests.isEmpty()) {
            for (Map.Entry<String, S> seq : sequence.entrySet()) {
                String seqName = seq.getKey();
                S seqItem = seq.getValue();

                producer.clearTests();
                producer.setName(getName().append(seqName));
                for (Map.Entry<String, ParametrizedSequenceTestable<P,S>> test :
                        tests.entrySet()) {
                    String testName = test.getKey();
                    ParametrizedSequenceTestable<P,S> testable = test.getValue();

                    producer.addTest(testName,
                            new ParametrizedSequenceTestableImpl<>(
                                    testable, seqItem));
                }

                final Map<ComposedName, A> performance =
                        producer.execute().getTree();
                map.put(getName().append(seqName), performance);
            }
        }
        producer.clearTests();
        dispatchToConsumers(getName(), map);
        return new TreeHolder<>(getName(), map, stringGenerator);
    }

    private static class ParametrizedSequenceTestableImpl<P,S>
            extends ParametrizedTestable<P> {
        private final ParametrizedSequenceTestable<P,S> test;
        private final S sequenceItem;

        public ParametrizedSequenceTestableImpl(
                ParametrizedSequenceTestable<P, S> test, S seqItem) {
            this.test = test;
            this.sequenceItem = seqItem;
        }

        /** Called before each test run to initialize the {@code param}. */
        @Override
        public void setUp(P param) {
            test.setUp(param, sequenceItem);
        }

        /**
         * Called before each sample of tests, its time is not
         * accounted.
         */
        @Override
        public void onBeforeSample(P param, int iterations) {
            test.beforeTest(param, sequenceItem, iterations);
        }

        /** Contains the test. */
        @Override
        public Object test(P param) {
            return test.test(param, sequenceItem);
        }
    }
}
