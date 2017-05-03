package com.fillumina.performance.param;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.annotation.Sequence;
import com.fillumina.performance.util.TName;
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

        final TName experimentName = getName();

        //TODO create a generic string generator for trees (mem, speed...)
        PHolder.Builder<A> builder =
                PHolder.<A>builder(experimentName/*, stringGenerator*/);

        //      test name,          options
        LinkedMap<TName, LinkedMap<TName, Runnable>> sequencedTestMap =
                new LinkedMap<>();

        for (Map.Entry<TName, Runnable> entry : getTests().entrySet()) {
            TName testName = entry.getKey();
            Runnable runnable = entry.getValue();

            LinkedMap<TName, Runnable> runnableList = ParameterHelper.
                        createParameterizedRunnable(
                                runnable, sequences, Sequence.class);

            sequencedTestMap.put(testName, runnableList);
        }

        int sequenceSize = sequencedTestMap.getEntryAtIndex(0).getValue().size();
        for (int i=0; i<sequenceSize; i++) {
            producer.clearTests();
            for (Entry<TName, LinkedMap<TName, Runnable>> entry :
                    sequencedTestMap) {
                TName testName = entry.getKey();
                final LinkedMap.LinkedEntry<TName, Runnable> paramTestEntry =
                        entry.getValue().getEntryAtIndex(i);
                TName paramName = paramTestEntry.getKey();
                Runnable paramTest = paramTestEntry.getValue();

                producer.setName(paramName);
                producer.addTest(testName, paramTest);
            }
            builder.addChild(producer.execute());
        }

        PHolder<A> holder = builder.build();
        return holder;
    }
}