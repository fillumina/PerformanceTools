package com.fillumina.performance.suite.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParametrizedSequencePerformance
        implements PerformanceConsumer
            <Map<String, Map<String,PerformanceStats>>> {

    private final Map<String, AssertParametrizedPerformance> map =
            new LinkedHashMap<>();
    private AssertParametrizedPerformance allParametrizedPerformanceAssertion;

    public AssertParametrizedSequencePerformance forAllSequences(
            AssertParametrizedPerformance parametrizedPerformanceAssertion) {
        allParametrizedPerformanceAssertion = parametrizedPerformanceAssertion;
        return this;
    }

    public AssertParametrizedSequencePerformance forSequence(String sequence,
            AssertParametrizedPerformance parametrizedPerformanceAssertion) {
        map.put(sequence, parametrizedPerformanceAssertion);
        return this;
    }

    @Override
    public void consume(String message,
            Map<String, Map<String, PerformanceStats>> performances) {
        for (Map.Entry<String, Map<String, PerformanceStats>> entry :
                performances.entrySet()) {
            String testName = entry.getKey();
            Map<String, PerformanceStats> parametrizedStats = entry.getValue();

            AssertParametrizedPerformance assertion = map.get(testName);
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
