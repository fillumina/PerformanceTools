package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fra
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

    public AssertPerformance forExecution(final String name) {
        final AssertPerformance assertPerformance =
                AssertPerformance.withTolerance(percentageTolerance);
        map.put(name, assertPerformance);
        return assertPerformance;
    }

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        final AssertPerformance assertPerformance = map.get(message);
        assertPerformance.consume(message, loopPerformances);
    }
}
