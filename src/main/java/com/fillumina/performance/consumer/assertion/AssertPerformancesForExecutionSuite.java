package com.fillumina.performance.consumer.assertion;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public class AssertPerformancesForExecutionSuite
        extends AbstractAssertPerformanceSuite<AssertPerformancesForExecutionSuite> {
    private static final long serialVersionUID = 1L;

    private Map<String, AssertPerformance> map = new HashMap<>();

    public AssertPerformance forExecution(final String name) {
        final AssertPerformance assertPerformance = new AssertPerformance();
        assertPerformance.setMessage(name);
        assertPerformance.setPercentageTolerance(getPercentageTolerance());
        map.put(name, assertPerformance);
        return assertPerformance;
    }

    @Override
    public void process() {
        final AssertPerformance assertPerformance = map.get(getMessage());
        processAssertions(assertPerformance);
    }
}
