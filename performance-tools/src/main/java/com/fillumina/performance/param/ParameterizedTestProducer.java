package com.fillumina.performance.param;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractPerformanceInstrumentable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.annotation.Param;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedTestProducer<A extends Assertable>
    extends AbstractPerformanceInstrumentable<ParameterizedTestProducer<A>,A> {

    private static final long serialVersionUID = 1L;
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
    public PHolder<A> execute() {
        if (params == null || params.isEmpty()) {
            return executeProducer();
        }

        assertTestsPresent();

        PHolder.Builder<A> builder = PHolder.<A>experiment(getName());

        StatsProducer<A> producer = getProducer();
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

            builder.addSubExperiment(producer.execute());
        }
        PHolder<A> holder = builder.build();
        return holder;
    }
}