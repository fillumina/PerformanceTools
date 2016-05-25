package com.fillumina.performance.suite;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.StatsProducer;
import com.fillumina.performance.suite.viewer.StringTableParametrizedStatsViewer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Instrumenter that allows to execute a parametrized test.
 * If a test has been already added to the
 * {@link com.fillumina.performance.sample.DefaultPerformanceTimer}
 * it will be executed alongside the parametrized
 * one defined by this class.
 * Applying this class to the right instrumenter allows to execute the tests
 * in a single-threaded or multi-threaded environment
 * (see {@link com.fillumina.performance.PerformanceTimerFactory}).
 *
 * @param P type of the test parameter
 * @author Francesco Illuminati
 */
public class ParametrizedPerformanceSuite<P>
        extends AbstractPerformanceProducer
            <ParametrizedPerformanceSuite<P>,
             Map<ComposedName, PerformanceStats>,
             ParametrizedTestable<P>>
        implements ParameterContainer<P>,
            ParametrizedStatsProducer<P>,
            Instrumenter<StatsProducer> {

    private StatsProducer producer;
    private final Map<String, P> params = new LinkedHashMap<>();

    /**
     * Add a parameter to the test.
     * @param name  parameter's name or description
     * @param param  parameter
     * @return {@code this} to allow for <i>fluent interface</i>
     */
    @SuppressWarnings("unchecked")
    @Override
    public ParametrizedPerformanceSuite<P> addParameter(
            final String name, final P param) {
        params.put(name, param);
        return this;
    }

    protected Map<String, P> getParams() {
        return params;
    }

    @Override
    public ParametrizedPerformanceSuite<P> instrument(
            StatsProducer instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    @Override
    public <T extends Instrumenter<ParametrizedStatsProducer<P>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    protected PerformanceProducer<PerformanceStats,Testable>
            getPerformanceProducer() {
        return producer;
    }

    @Override
    public PerformanceHolder<Map<ComposedName, PerformanceStats>> execute() {
        Map<ComposedName, PerformanceStats> map = new LinkedHashMap<>();
        for (Map.Entry<String, ParametrizedTestable<P>> entry :
                getTests().entrySet()) {
            String testName = entry.getKey();
            ParametrizedTestable<P> parametrizedTestable = entry.getValue();

            producer.resetTests();
            producer.setName(getName().add(testName));
            addParametersToTest(parametrizedTestable);
            map.put(getName().add(testName),
                    producer.execute().getPerformance());
        }
        dispatchToConsumers(getName(), map);
        return new PerformanceHolder<>(getName(), map,
                StringTableParametrizedStatsViewer.INSTANCE);
    }

    protected void addParametersToTest(
            ParametrizedTestable<P> parametrizedTestable) {
        producer.resetTests();
        for (Map.Entry<String, P> param : getParams().entrySet()) {
            String paramName = param.getKey();
            P parameter = param.getValue();
            Testable test = new ParametrizedTestableImpl<>(
                            parametrizedTestable,
                            parameter);
            producer.addTest(paramName, test);
        }
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
