package com.fillumina.performance.suite.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParametrizedSequencePerformance<T>
        implements PerformanceConsumer
            <Map<String, Map<String,PerformanceStats>>> {

    private final T caller;
    private final Map<String,
            AssertParametrizedPerformance<AssertParametrizedSequencePerformance<T>>>
            map = new LinkedHashMap<>();
    private AssertParametrizedPerformance<AssertParametrizedSequencePerformance<T>>
            allParametrizedPerformanceAssertion;

    public static AssertParametrizedSequencePerformance<?> create() {
        return new AssertParametrizedSequencePerformance<>();
    }

    public AssertParametrizedSequencePerformance() {
        this(null);
    }

    public AssertParametrizedSequencePerformance(T caller) {
        this.caller = caller;
    }

    public AssertParametrizedPerformance<AssertParametrizedSequencePerformance<T>>
            forAllSequences() {
        allParametrizedPerformanceAssertion =
                new AssertParametrizedPerformance<>(this);
        return allParametrizedPerformanceAssertion;
    }

    public AssertParametrizedPerformance<AssertParametrizedSequencePerformance<T>>
            forSequence(String sequence) {
        AssertParametrizedPerformance<AssertParametrizedSequencePerformance<T>>
                parametrizedPerformanceAssertion =
                    new AssertParametrizedPerformance<>(this);
        map.put(sequence, parametrizedPerformanceAssertion);
        return parametrizedPerformanceAssertion;
    }

    public T end() {
        return caller;
    }

    @Override
    public void consume(String message,
            Map<String, Map<String, PerformanceStats>> performances) {
        for (Map.Entry<String, Map<String, PerformanceStats>> entry :
                performances.entrySet()) {
            String testName = entry.getKey();
            Map<String, PerformanceStats> parametrizedStats = entry.getValue();

            AssertParametrizedPerformance<?> assertion = map.get(testName);
            if (assertion != null) {
                assertion.consume(testName, parametrizedStats);
            }
            if (allParametrizedPerformanceAssertion != null) {
                allParametrizedPerformanceAssertion.consume(message,
                        parametrizedStats);
            }
        }
    }
}
