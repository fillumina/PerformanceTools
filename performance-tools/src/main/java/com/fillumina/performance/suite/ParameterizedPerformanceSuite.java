package com.fillumina.performance.suite;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.infrastructure.type.AssertableParameterizedStats;
import com.fillumina.performance.infrastructure.type.AssertableStats;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Instrumenter that allows to execute a parameterized test.
 * If a test has been already added to the
 * {@link com.fillumina.performance.speed.sample.DefaultPerformanceTimer}
 * it will be executed alongside the parameterized
 * one defined by this class.
 * Applying this class to the right instrumenter allows to execute the tests
 * in a single-threaded or multi-threaded environment
 * (see {@link com.fillumina.performance.PerformanceTimerFactory}).
 *
 * @param P test parameter
 * @param A result statistics
 * @param I instrumented statistics
 *
 * @author Francesco Illuminati
 */
public class ParameterizedPerformanceSuite
            <P,
             A extends AssertableParameterizedStats,
             I extends AssertableStats>
        extends AbstractPerformanceProducer
            <ParameterizedPerformanceSuite<P,A,I>,
             A,
             ParameterizedTestable<P>>
        implements ParameterContainer<P>,
            ParameterizedStatsProducer<P,A>,
            Instrumenter<StatsProducer<I>>,
            Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, P> params = new LinkedHashMap<>();
    private final StringGenerator<A> stringGenerator;
    private StatsProducer<? extends AssertableStats> producer;

    public ParameterizedPerformanceSuite(StringGenerator<A> stringGenerator) {
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
    public ParameterizedPerformanceSuite<P,A,I> addParameter(
            final String name, final P param) {
        params.put(name, param);
        return this;
    }

    @Override
    public <T extends Instrumenter<ParameterizedStatsProducer<P, A>>> T
            instrumentedBy(T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    @Override
    public ParameterizedPerformanceSuite<P,A,I> instrument(
            StatsProducer<I> instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    @Override
    public PHolder<A> execute() {
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test found");
        }

        PHolder<A> performances =
                new PHolder<>(getName(), stringGenerator);

        for (Map.Entry<String, ParameterizedTestable<P>> entry :
                getTests().entrySet()) {
            String testName = entry.getKey();
            ParameterizedTestable<P> parameterizedTestable = entry.getValue();

            final ComposedName composedName = getName().append(testName);
            producer.setName(composedName);
            addParametersToTest(parameterizedTestable);
            performances.addChild(producer.execute());
        }
        dispatchToConsumers(performances);
        return performances;
    }

    protected void addParametersToTest(
            ParameterizedTestable<P> parameterizedTestable) {
        producer.clearTests();
        if (params.isEmpty()) {
            throw new IllegalStateException("no parameter found");
        } else {
            for (Map.Entry<String, P> param : params.entrySet()) {
                final String paramName = param.getKey();
                final P paramValue = param.getValue();
                addParameterizedTestable(paramName,
                        paramValue,
                        parameterizedTestable);
            }
        }
    }

    private void addParameterizedTestable(String paramName, P parameter,
            ParameterizedTestable<P> parameterizedTestable) {
        Testable test = new ParameterizedTestableImpl<>(
                parameterizedTestable,
                parameter);
        producer.addTest(paramName, test);
    }

    private static class ParameterizedTestableImpl<P> implements Testable {
        private final ParameterizedTestable<P> test;
        private final P param;

        public  ParameterizedTestableImpl(
                ParameterizedTestable<P> parameterizedTest, P param) {
            this.test = parameterizedTest;
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

    @Override
    public String toString() {
        TableFormatter tf = new TableFormatter();
        tf.header("paramenters", TableFormatter.Alignment.LEFT, '-');
        for (Map.Entry<String,P> entry: params.entrySet()) {
            String name = entry.getKey();
            P param = entry.getValue();
            tf.cell(name, ":").cell(param).endl();
        }
        return tf.toString();
    }
}
