package com.fillumina.performance.producer.suite;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.DefaultInstrumenterPerformanceProducer;
import com.fillumina.performance.producer.LoopPerformances;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractParametrizedInstrumenterSuite
            <T extends AbstractParametrizedInstrumenterSuite<T>>
        extends DefaultInstrumenterPerformanceProducer<T>{
    private static final long serialVersionUID = 1L;

    private Map<String, LoopPerformances> resultLoopPerformance =
            new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public T use(final PerformanceConsumer consumer) {
        for (Map.Entry<String, LoopPerformances> entry: resultLoopPerformance.entrySet()) {
            final String name = entry.getKey();
            final LoopPerformances lp = entry.getValue();

            consumer.consume(name, lp);
        }
        return (T) this;
    }

    protected void addTestLoopPerformances(
            final String name,
            final LoopPerformances loopPerformances) {
        resultLoopPerformance.put(name, loopPerformances);
    }
}
