package com.fillumina.performance.sample.suite;

import com.fillumina.performance.sample.AbstractPerformanceTimer;
import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceSampleProducer;
import com.fillumina.performance.sample.PerformanceSampleProducerInstrumenter;
import com.fillumina.performance.sample.PerformanceTimer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An helper with common logic to be inherited by suits.
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractParametrizedInstrumenterSuite
            <S extends AbstractParametrizedInstrumenterSuite<S,T,P>,T,P>
        extends AbstractPerformanceTimer<S,T>
        implements ParameterContainer<P>, PerformanceSampleProducer,
            PerformanceSampleProducerInstrumenter {

    private DefaultPerformanceTimer performanceTimer;
    private final Map<String, P> params = new LinkedHashMap<>();

    protected abstract void createTests();

    /**
     * Add a parameter to the test.
     * @param name  parameter's name or description
     * @param param  parameter
     * @return {@code this} to allow for <i>fluent interface</i>
     */
    @SuppressWarnings("unchecked")
    @Override
    public S addParameter(final String name, final P param) {
        params.put(name, param);
        return (S) this;
    }

    protected Map<String, P> getParams() {
        return params;
    }

    /** Requires a {@link PerformanceTimer}. */
    @Override
    @SuppressWarnings("unchecked")
    public S instrument(PerformanceSampleProducer performanceSampleProducer) {
        if (! (performanceSampleProducer instanceof DefaultPerformanceTimer)) {
            throw new IllegalArgumentException(
                    PerformanceTimer.class.getSimpleName() + " expected");
        }
        this.performanceTimer = (DefaultPerformanceTimer) performanceSampleProducer;
        return (S) this;
    }

    protected DefaultPerformanceTimer getPerformanceTimer() {
        return performanceTimer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S warmup(int iterations) {
        performanceTimer.warmup(iterations);
        return (S) this;
    }

    @Override
    public PerformanceSample execute(int iterations) {
        createTests();
        return getPerformanceTimer().execute(iterations);
    }
}
