package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsProducer;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.tname.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SequencedTestProducer
    extends AbstractStatsProducerInstrumenter<SequencedTestProducer, Stats<?>> {

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
    public MixedAssertableHolder get() {
        if (sequences == null || sequences.isEmpty()) {
            return executeProducer();
        }

        assertTestsPresent();

        //      test name,          options
        LinkedMap<TName, LinkedMap<TName, Runnable>> sequencedTestMap =
                new LinkedMap<>();

        getTests().forEach((TName testName, Runnable runnable) -> {
            LinkedMap<TName, Runnable> runnableList =
                    ParameterHelper.createParameterizedRunnable(
                            runnable, sequences, Sequence.class);

            sequencedTestMap.put(testName, runnableList);
        });

        final TName experimentName = getName();

        MixedAssertableHolder.Joiner joiner =
                MixedAssertableHolder.joiner(getName());

        StatsProducer<?, ?> producer = getProducer();
        int sequenceSize = sequencedTestMap.getEntryAtIndex(0).getValue().size();
        for (int i=0; i<sequenceSize; i++) {
            final int index = i;
            producer.clearTests();
            sequencedTestMap.forEach(
                    (TName testName, LinkedMap<TName, Runnable> map) -> {
                final LinkedMap.LinkedEntry<TName, Runnable> paramTestEntry =
                        map.getEntryAtIndex(index);
                TName paramName = paramTestEntry.getKey();
                Runnable paramTest = paramTestEntry.getValue();

                TName fullName = paramName.size() > 1 ?
                        paramName : experimentName.append(paramName);

                producer.setName(fullName);
                producer.addTest(paramName.append(testName), paramTest);
            });
            joiner.addSubExperiment(producer.get());
        }

        MixedAssertableHolder mixedHolder = joiner.join();
        return mixedHolder;
    }
}