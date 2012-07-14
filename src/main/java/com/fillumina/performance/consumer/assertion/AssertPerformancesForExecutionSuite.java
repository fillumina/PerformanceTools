package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public class AssertPerformancesForExecutionSuite
        implements PerformanceConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, AssertPerformance> map = new HashMap<>();
    private final float percentageTolerance;

    public static AssertPerformancesForExecutionSuite withTolerance(
            final float tolerancePercentage) {
        return new AssertPerformancesForExecutionSuite(tolerancePercentage);
    }

    public AssertPerformancesForExecutionSuite() {
        this(AssertPerformance.SAFE_TOLERANCE);
    }

    private AssertPerformancesForExecutionSuite(
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
