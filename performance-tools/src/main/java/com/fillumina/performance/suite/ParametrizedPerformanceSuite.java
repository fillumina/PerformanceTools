package com.fillumina.performance.suite;

import com.fillumina.performance.assertion.AssertableMultiTest;
import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Instrumenter that allows to execute a parametrized test.
 * If a test has been already added to the
 * {@link com.fillumina.performance.speed.sample.DefaultPerformanceTimer}
 * it will be executed alongside the parametrized
 * one defined by this class.
 * Applying this class to the right instrumenter allows to execute the tests
 * in a single-threaded or multi-threaded environment
 * (see {@link com.fillumina.performance.PerformanceTimerFactory}).
 *
 * @param P type of the test parameter
 * @author Francesco Illuminati
 */
public class ParametrizedPerformanceSuite<P,A extends AssertableMultiTest>
        extends AbstractPerformanceProducer
            <ParametrizedPerformanceSuite<P,A>,
             Map<ComposedName, A>,
             ParametrizedTestable<P>>
        implements ParameterContainer<P>,
            ParametrizedStatsProducer<P,A>,
            Instrumenter<StatsProducer<A>>,
            Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, P> params = new LinkedHashMap<>();
    private final StringGenerator<Map<ComposedName, A>> stringGenerator;
    private StatsProducer<A> producer;

    public ParametrizedPerformanceSuite(
            StringGenerator<Map<ComposedName, A>> stringGenerator) {
        this.stringGenerator = stringGenerator;
    }

    /**
     * Add a parameter to the test.
     * @param name  parameter's name or description
     * @param param  parameter
     * @return {@code this} to allow for <i>fluent interface</i>
     */
    @SuppressWarnings("unchecked")
    @Override
    public ParametrizedPerformanceSuite<P,A> addParameter(
            final String name, final P param) {
        params.put(name, param);
        return this;
    }

    @Override
    public ParametrizedPerformanceSuite<P,A> instrument(
            StatsProducer<A> instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    @Override
    public <T extends Instrumenter<ParametrizedStatsProducer<P,A>>> T
                instrumentedBy(T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    protected PerformanceProducer<A,Testable> getPerformanceProducer() {
        return producer;
    }

    @Override
    public PerformanceHolder<Map<ComposedName, A>> execute() {
        Map<ComposedName, A> map = new LinkedHashMap<>();
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test found");
        }
        for (Map.Entry<String, ParametrizedTestable<P>> entry :
                getTests().entrySet()) {
            String testName = entry.getKey();
            ParametrizedTestable<P> parametrizedTestable = entry.getValue();

            final ComposedName composedName = getName().append(testName);
            producer.setName(composedName);
            addParametersToTest(parametrizedTestable);
            map.put(composedName, producer.execute().getPerformance());
        }
        dispatchToConsumers(getName(), map);
        return new PerformanceHolder<>(getName(), map, stringGenerator);
    }

    protected void addParametersToTest(
            ParametrizedTestable<P> parametrizedTestable) {
        producer.clearTests();
        if (params.isEmpty()) {
            throw new IllegalStateException("no parameter found");
        } else {
            for (Map.Entry<String, P> param : params.entrySet()) {
                final String paramName = param.getKey();
                final P paramValue = param.getValue();
                addParametrizedTestable(paramName,
                        paramValue,
                        parametrizedTestable);
            }
        }
    }

    private void addParametrizedTestable(String paramName, P parameter,
            ParametrizedTestable<P> parametrizedTestable) {
        Testable test = new ParametrizedTestableImpl<>(
                parametrizedTestable,
                parameter);
        producer.addTest(paramName, test);
    }

    private static class ParametrizedTestableImpl<P> implements Testable {
        private final ParametrizedTestable<P> test;
        private final P param;

        public  ParametrizedTestableImpl(
                ParametrizedTestable<P> parametrizedTest, P param) {
            this.test = parametrizedTest;
            this.param = param;
        }

        @Override
        public void setUp() {
            test.setUp(param);
        }

        @Override
        public void onBeforeSample(int iterations) {
            test.onBeforeSample(param, iterations);
        }

        @Override
        public Object test() {
            return test.test(param);
        }
    }
}
