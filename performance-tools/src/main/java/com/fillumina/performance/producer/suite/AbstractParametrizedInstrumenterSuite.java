package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.DefaultInstrumenterPerformanceProducer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.LoopPerformances;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An helper with common logic to be inherited by suits.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractParametrizedInstrumenterSuite
            <T extends AbstractParametrizedInstrumenterSuite<T,P>, P>
        extends DefaultInstrumenterPerformanceProducer<T>
        implements ParametersContainer<T,P> {
    private static final long serialVersionUID = 1L;

    private static class NamedParameter<P> {
        final String name;
        final P param;

         NamedParameter(final String name, final P param) {
            this.name = name;
            this.param = param;
        }
    }

    private Map<String, LoopPerformances> resultLoopPerformance =
            new LinkedHashMap<>();

    private List<NamedParameter<P>> parameters = new ArrayList<>();

    protected void addTestLoopPerformances(
            final String name,
            final LoopPerformances loopPerformances) {
        resultLoopPerformance.put(name, loopPerformances);
    }

    /** @return a map with the test's result for each parameter (by name). */
    public Map<String, LoopPerformances> getTestLoopPerformances() {
        return Collections.unmodifiableMap(resultLoopPerformance);
    }

    /**
     * Add a parameter to the test.
     * @param name  parameter's name or description
     * @param param  parameter
     * @return {@code this} to allow for <i>fluent interface</i>
     */
    @Override
    @SuppressWarnings("unchecked")
    public T addParameter(final String name, final P param) {
        parameters.add(new NamedParameter<>(name, param));
        return (T) this;
    }

    /** Wrap a parameter into a {@link Runnable} to be used as test. */
    protected abstract Runnable wrap(final Object param);

    protected void addTestsToPerformanceExecutor() {
        final InstrumentablePerformanceExecutor<?> ipe =
                getPerformanceExecutor();
        if (ipe == null) {
            throw new IllegalStateException("You must specify a " +
                    "PerformanceExecutor via instrument()");
        }
        // creates a different test for each parameter and add it to the
        // executor.
        for (final NamedParameter<P> np: parameters) {
            ipe.addTest(np.name, wrap(np.param));
        }
        parameters.clear();
    }
}
