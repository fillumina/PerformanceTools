package com.fillumina.performance.consumer.assertion;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public class AssertPerformanceForIterationsSuite
        extends AbstractAssertPerformanceSuite<AssertPerformanceForIterationsSuite> {
    private static final long serialVersionUID = 1L;

    private Map<Long, AssertPerformance> map = new HashMap<>();

    public AssertPerformance forIterations(final long iterations) {
        final AssertPerformance assertPerformance = new AssertPerformance();
        assertPerformance.setPercentageTolerance(getPercentageTolerance());
        assertPerformance.setMessage(String.valueOf(iterations));
        map.put(iterations, assertPerformance);
        return assertPerformance;
    }

    @Override
    public void process() {
        final long iterations = getLoopPerformances().getIterations();
        final AssertPerformance assertPerformance = map.get(iterations);
        processAssertions(assertPerformance);
    }
}
