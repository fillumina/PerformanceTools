package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsProducer;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.tname.TName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedTestProducer
    extends AbstractStatsProducerInstrumenter<ParameterizedTestProducer, Stats<?>> {
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
    public MixedAssertableHolder get() {
        if (params == null || params.isEmpty()) {
            return executeProducer();
        }

        assertTestsPresent();

        MixedAssertableHolder.Joiner joiner =
                MixedAssertableHolder.joiner(getName());

        StatsProducer<?, ?> producer = getProducer();
        for (Map.Entry<TName, Runnable> entry : getTests().entrySet()) {
            TName testName = entry.getKey();
            Runnable runnable = entry.getValue();

            final TName composedName = getName().append(testName);
            producer.clearTests();
            producer.setName(composedName);

            LinkedMap<TName, Runnable> runnableMap =
                    ParameterHelper.createParameterizedRunnable(
                                runnable, params, Param.class);

            for (Map.Entry<TName, Runnable> e : runnableMap) {
                final TName tname = e.getKey();
                final Runnable test = e.getValue();
                producer.addTest(tname, test);
            }

            MixedAssertableHolder result = producer.get();
            joiner.addSubExperiment(result);
        }
        MixedAssertableHolder mixedHolder = joiner.join();
        return mixedHolder;
    }
}