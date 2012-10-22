package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertPerformanceForExecutionSuite
        implements PerformanceConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, AssertPerformance> map = new HashMap<>();
    private final float percentageTolerance;

    public static AssertPerformanceForExecutionSuite withTolerance(
            final float tolerancePercentage) {
        return new AssertPerformanceForExecutionSuite(tolerancePercentage);
    }

    public AssertPerformanceForExecutionSuite() {
        this(AssertPerformance.SAFE_TOLERANCE);
    }

    private AssertPerformanceForExecutionSuite(
            final float percentageTolerance) {
        this.percentageTolerance = percentageTolerance;
    }

    /** Sets the test to assert. */
    public AssertPerformance forExecution(final String testName) {
        final AssertPerformance assertPerformance =
                AssertPerformance.withTolerance(percentageTolerance);
        map.put(testName, assertPerformance);
        return assertPerformance;
    }

    @Override
    public void consume(final String testName,
            final LoopPerformances loopPerformances) {
        final AssertPerformance assertPerformance = map.get(testName);
        if (assertPerformance == null) {
            throw new RuntimeException("Test '" + testName + "' does not exist!");
        }
        assertPerformance.consume(testName, loopPerformances);
    }
}
