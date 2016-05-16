package com.fillumina.performance.suite;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.suite.viewer.StringTableParametrizedSequenceStatsViewer;
import com.fillumina.performance.util.instrument.Instrumenter;
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
        extends AbstractPerformanceProducer
            <ParametrizedSequencePerformanceSuite<P,S>,
             Map<String, Map<String, PerformanceStats>>,
             ParametrizedSequenceTestable<P,S>>
        implements ParametrizedSequenceStatsProducer<P,S>,
            SequenceContainer<S>,
            Instrumenter<ParametrizedStatsProducer<P>> {

    private final Map<String, S> sequence = new LinkedHashMap<>();
    private ParametrizedStatsProducer<P> producer;

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

    @Override
    public <T extends Instrumenter<ParametrizedSequenceStatsProducer<P, S>>>
            T instrumentedBy(T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    @Override
    public ParametrizedSequencePerformanceSuite<P,S> instrument(
            ParametrizedStatsProducer<P> instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    @Override
    public PerformanceHolder<Map<String, Map<String, PerformanceStats>>> execute() {
        Map<String,Map<String,PerformanceStats>> map = new LinkedHashMap<>();
        Map<String, ParametrizedSequenceTestable<P,S>> tests = getTests();
        if (!tests.isEmpty()) {
            for (Map.Entry<String, S> seq : sequence.entrySet()) {
                String seqName = seq.getKey();
                S seqItem = seq.getValue();

                producer.resetTests();
                producer.setName(seqName);

                for (Map.Entry<String, ParametrizedSequenceTestable<P,S>> test :
                        tests.entrySet()) {
                    String testName = test.getKey();
                    ParametrizedSequenceTestable<P,S> testable = test.getValue();

                    producer.addTest(testName,
                            new ParametrizedSequenceTestableImpl<>(
                                    testable, seqItem));
                }

                final Map<String, PerformanceStats> performance =
                        producer.execute().getPerformance();
                map.put(seqName, performance);
            }
        }
        producer.resetTests();
        dispatchToConsumers(getName(), map);
        return new PerformanceHolder<>(getName(), map,
                StringTableParametrizedSequenceStatsViewer.INSTANCE);
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
