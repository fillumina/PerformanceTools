package com.fillumina.performance.param;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.annotation.Sequence;
import com.fillumina.performance.util.TreeName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SequencedTestProducer<A extends Assertable>
        extends AbstractPerformanceProducer<SequencedTestProducer<A>,
                                            A,
                                            Runnable>
        implements Instrumenter<StatsProducer<A>>,
                   Serializable {

    private static final long serialVersionUID = 1L;
    public static final String SEPARATOR = "-";

    private StatsProducer<A> producer;
    private final LinkedTree<String,Object> sequences;

    public SequencedTestProducer(LinkedTree<String,Object> sequences) {
        this.sequences = sequences;
    }

    @Override
    public SequencedTestProducer<A> instrument(
            StatsProducer<A> instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    @Override
    public PHolder<A> execute() {
        if (sequences == null || sequences.isEmpty()) {
            return producer.execute();
        }

        assertTestsPresent();

        final TreeName experimentName = getName();

        //TODO create a generic string generator for trees (mem, speed...)
        PHolder<A> performances = new PHolder<>(experimentName/*, stringGenerator*/);

        //      test name,          options
        LinkedMap<String, LinkedMap<TreeName, Runnable>> sequencedTestMap =
                new LinkedMap<>();

        for (Map.Entry<String, Runnable> entry : getTests().entrySet()) {
            String testName = entry.getKey();
            Runnable runnable = entry.getValue();

            LinkedMap<TreeName, Runnable> runnableList = ParameterHelper.
                        createParameterizedRunnable(
                                runnable, sequences, Sequence.class);

            sequencedTestMap.put(testName, runnableList);
        }

        int sequenceSize = sequencedTestMap.getEntryAtIndex(0).getValue().size();
        for (int i=0; i<sequenceSize; i++) {
            producer.clearTests();
            for (Entry<String, LinkedMap<TreeName, Runnable>> entry :
                    sequencedTestMap) {
                String testName = entry.getKey();
                final LinkedMap.LinkedEntry<TreeName, Runnable> paramTestEntry =
                        entry.getValue().getEntryAtIndex(i);
                TreeName paramName = paramTestEntry.getKey();
                Runnable paramTest = paramTestEntry.getValue();

                producer.setName(paramName);
                producer.addTest(testName, paramTest);
            }
            performances.addChild(producer.execute());
        }

        dispatchToConsumers(performances);
        return performances;
    }
}