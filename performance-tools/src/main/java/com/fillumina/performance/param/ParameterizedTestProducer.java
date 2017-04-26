package com.fillumina.performance.param;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.annotation.Param;
import com.fillumina.performance.util.TreeName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedTestProducer<A extends Assertable>
        extends AbstractPerformanceProducer<ParameterizedTestProducer<A>,
                                            A,
                                            Runnable>
        implements Instrumenter<StatsProducer<A>>,
                   Serializable {

    private static final long serialVersionUID = 1L;
    public static final String SEPARATOR = "-";

    private StatsProducer<A> producer;
    private final LinkedTree<String,Object> params;

    public ParameterizedTestProducer(LinkedTree<String,Object> params) {
        this.params = params;
    }

    @Override
    public ParameterizedTestProducer<A> instrument(
            StatsProducer<A> instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    @Override
    public PHolder<A> execute() {
        if (params == null || params.isEmpty()) {
            return producer.execute();
        }

        assertTestsPresent();

        //TODO create a generic string generator for trees (mem, speed...)
        PHolder<A> performances = new PHolder<>(getName()/*, stringGenerator*/);

        for (Map.Entry<String, Runnable> entry : getTests().entrySet()) {
            String testName = entry.getKey();
            Runnable runnable = entry.getValue();

            final TreeName composedName = getName().append(testName);
            producer.setName(composedName);
            producer.clearTests();

            LinkedMap<TreeName, Runnable> runnableList =
                    ParameterHelper.createParameterizedRunnable(
                                runnable, params, Param.class);

            for (Map.Entry<TreeName, Runnable> e : runnableList) {
                final Runnable test = e.getValue();
                final String fullTestName = e.getKey()
                        .toStringWithSeparator(SEPARATOR);
                producer.addTest(fullTestName, test);
            }

            performances.addChild(producer.execute());
        }
        dispatchToConsumers(performances);
        return performances;
    }
}