package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsProducer;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.tname.TName;

/**
 * Tests with different parameter values are executed together and their
 * results are presented on a single statistic.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedTestProducer
    extends AbstractStatsProducerInstrumenter<ParameterizedTestProducer> {
    public static final String PARAMETERS = "parameters";
    public static final String SEPARATOR = "-";

    private final LinkedTree<String,Object> params;

    public interface Configuration {
        LinkedTree<String,Object> getParameters();
    }

    public ParameterizedTestProducer(Configuration config) {
        this(config.getParameters());
    }

    public ParameterizedTestProducer(LinkedTree<String,Object> params) {
        this.params = params;
    }

    @Override
    public MixedStatsHolder get() {
        if (params == null || params.isEmpty()) {
            return executeProducer();
        }

        assertTestsPresent();

        TName name = getName();
        MixedStatsHolder.Joiner joiner = MixedStatsHolder.joiner(name);

        StatsProducer<?> producer = getProducer();
        getTests().forEach((TName testName, Runnable runnable) -> {
            final TName composedName = createTestName(name, testName);
            producer.clearTests();
            producer.setName(composedName);

            IndexedHashMap<TName, RunnableOptionsContainer> runnableMap =
                    ParameterHelper.createParameterizedRunnables(
                            Param.class, params, runnable);

            runnableMap.forEach( (TName tname, RunnableOptionsContainer rc) ->
                producer.addTest(createTestName(composedName, tname),
                        rc.getRunnable()));

            MixedStatsHolder result = producer.get();

            ParameterHelper.addOptionsToStats(PARAMETERS,
                    runnableMap, result);

            joiner.addSubExperiment(result);
        });
        MixedStatsHolder mixedHolder = joiner.join();
        return mixedHolder;
    }
}