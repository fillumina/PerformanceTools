package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsProducer;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.tname.TName;
import java.util.Map.Entry;

/**
 * Tests with different sequence values are executed separately and
 * results are presented on different statistic. i.e. a sequence might be
 * the size of a map to be tested.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SequencedTestProducer
    extends AbstractStatsProducerInstrumenter<SequencedTestProducer> {

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
    public MixedStatsHolder get() {
        if (sequences == null || sequences.isEmpty()) {
            return executeProducer();
        }

        assertTestsPresent();

        //          test name,          options
        IndexedHashMap<TName, IndexedHashMap<TName, Runnable>> sequencedTestMap =
                new IndexedHashMap<>();

        getTests().forEach((TName testName, Runnable runnable) -> {
            IndexedHashMap<TName, Runnable> runnableList =
                    ParameterHelper.createParameterizedRunnable(
                            runnable, sequences, Sequence.class);

            sequencedTestMap.put(testName, runnableList);
        });

        final TName experimentName = getName();

        MixedStatsHolder.Joiner joiner =
                MixedStatsHolder.joiner(experimentName);

        StatsProducer<?> producer = getProducer();
        int sequenceSize = sequencedTestMap.getEntryAtIndex(0).getValue().size();
        for (int i=0; i<sequenceSize; i++) {
            final int index = i;
            producer.clearTests();
            sequencedTestMap.forEach((TName testName, IndexedHashMap<TName, Runnable> map) -> {
                final Entry<TName, Runnable> paramTestEntry =
                        map.getEntryAtIndex(index);
                TName paramName = paramTestEntry.getKey();
                Runnable paramTest = paramTestEntry.getValue();

                TName name = experimentName.append(paramName);
                producer.setName(name);
                producer.addTest(createTestName(name, testName), paramTest);
            });
            joiner.addSubExperiment(producer.execute());
        }

        MixedStatsHolder mixedHolder = joiner.join();
        return mixedHolder;
    }
}