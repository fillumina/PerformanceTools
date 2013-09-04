package com.fillumina.performance.producer.suite;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.DefaultInstrumenterPerformanceProducer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.LoopPerformances;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractParametrizedInstrumenterSuite
            <T extends AbstractParametrizedInstrumenterSuite<T,P>, P>
        extends DefaultInstrumenterPerformanceProducer<T>{
    private static final long serialVersionUID = 1L;

    private static class NamedObject<P> {
        final String name;
        final P object;

         NamedObject(final String name, final P object) {
            this.name = name;
            this.object = object;
        }
    }

    private Map<String, LoopPerformances> resultLoopPerformance =
            new LinkedHashMap<>();

    private List<NamedObject<P>> objects = new ArrayList<>();

    protected void addTestLoopPerformances(
            final String name,
            final LoopPerformances loopPerformances) {
        resultLoopPerformance.put(name, loopPerformances);
    }

    @SuppressWarnings("unchecked")
    public T addObjectToTest(final String name, final P object) {
        objects.add(new NamedObject<>(name, object));
        return (T) this;
    }

    protected abstract Runnable wrap(final Object object);

    protected void addTestsToPerformanceExecutor() {
        final InstrumentablePerformanceExecutor<?> ipe =
                getPerformanceExecutor();
        if (ipe == null) {
            throw new IllegalStateException("You must specify a " +
                    "PerformanceExecutor via instrument()");
        }
        for (final NamedObject<P> no: objects) {
            ipe.addTest(no.name, wrap(no.object));
        }
        objects.clear();
    }
}
