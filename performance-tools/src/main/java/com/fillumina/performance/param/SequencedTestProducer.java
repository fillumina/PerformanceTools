package com.fillumina.performance.param;

import com.fillumina.performance.annotation.Sequence;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractPerformanceInstrumentable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SequencedTestProducer<A extends Assertable>
    extends AbstractPerformanceInstrumentable<SequencedTestProducer<A>,A> {

    private static final long serialVersionUID = 1L;
    public static final String SEPARATOR = "-";

    private final LinkedTree<String,Object> sequences;

    public interface Configuration {
        LinkedTree<String,Object> getSequences();
    }

    public SequencedTestProducer(Configuration config) {
        this(config.getSequences());
    }

    public SequencedTestProducer(LinkedTree<String,Object> sequences) {
        this.sequences = sequences;
    }

    @Override
    public PHolder<A> execute() {
        if (sequences == null || sequences.isEmpty()) {
            return executeProducer();
        }

        assertTestsPresent();

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

        final TName experimentName = getName();

        PHolder.Builder<A> builder =
                PHolder.<A>experiment(experimentName/*, stringGenerator*/);

        StatsProducer<A> producer = getProducer();
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

                TName fullName = experimentName.append(paramName);

                producer.setName(fullName);
                producer.addTest(testName, paramTest);
            }
            builder.addSubExperiment(producer.execute());
        }

        PHolder<A> holder = builder.build();
        return holder;
    }
}