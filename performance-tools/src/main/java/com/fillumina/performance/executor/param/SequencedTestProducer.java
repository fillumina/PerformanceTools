package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsProducer;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.pathname.PathName;
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

    public static final String SEQUENCES = "sequences";
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
        IndexedHashMap<PathName, IndexedHashMap<PathName, RunnableOptionsContainer>>
                sequencedTestMap = new IndexedHashMap<>();

        getTests().forEach((PathName testName, Runnable runnable) -> {
            IndexedHashMap<PathName, RunnableOptionsContainer> runnableContainersMap =
                    ParameterHelper.createParameterizedRunnables(
                            Sequence.class, sequences, runnable);

            sequencedTestMap.put(testName, runnableContainersMap);
        });

        final PathName experimentName = getPathName();

        MixedStatsHolder.Joiner joiner =
                MixedStatsHolder.joiner(experimentName);

        StatsProducer<?> producer = getProducer();
        int sequenceSize = sequencedTestMap.getEntryAtIndex(0).getValue().size();
        for (int i=0; i<sequenceSize; i++) {
            final int index = i;
            producer.clearTests();

            IndexedHashMap<PathName, RunnableOptionsContainer> runnableMap =
                    new IndexedHashMap<>();

            sequencedTestMap.forEach((PathName testName,
                        IndexedHashMap<PathName, RunnableOptionsContainer> map) -> {
                final Entry<PathName, RunnableOptionsContainer> paramTestEntry =
                        map.getEntryAtIndex(index);
                PathName paramName = paramTestEntry.getKey();
                RunnableOptionsContainer runnableContainer = paramTestEntry.getValue();
                Runnable paramTest = runnableContainer.getRunnable();

                runnableMap.put(testName, runnableContainer);

                PathName name = experimentName.append(paramName);
                producer.setPathName(name);
                producer.addTest(createTestName(name, testName), paramTest);
            });

            final MixedStatsHolder results = producer.execute();

            ParameterHelper.addOptionsToStats(SEQUENCES,
                    runnableMap, results);

            joiner.addSubExperiment(results);
        }

        MixedStatsHolder mixedHolder = joiner.join();
        return mixedHolder;
    }
}