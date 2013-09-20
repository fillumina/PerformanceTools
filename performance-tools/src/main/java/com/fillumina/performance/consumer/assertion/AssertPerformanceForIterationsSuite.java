package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Asserts condition about tests executed a specified number of times.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertPerformanceForIterationsSuite
        implements PerformanceConsumer, Serializable, SuiteIterationsAssertion {
    private static final long serialVersionUID = 1L;

    private final Map<Long, AssertPerformance> map = new HashMap<>();
    private final float percentageTolerance;

    public static SuiteIterationsAssertion withTolerance(
            final float tolerancePercentage) {
        return new AssertPerformanceForIterationsSuite(tolerancePercentage);
    }

    public AssertPerformanceForIterationsSuite() {
        this(AssertPerformance.SAFE_TOLERANCE);
    }

    private AssertPerformanceForIterationsSuite(
            final float percentageTolerance) {
        this.percentageTolerance = percentageTolerance;
    }

    @Override
    public PerformanceAssertion forIterations(final long iterations) {
        final AssertPerformance assertPerformance =
                AssertPerformance.withTolerance(percentageTolerance);
        map.put(iterations, assertPerformance);
        return assertPerformance;
    }

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        final long iterations = loopPerformances.getIterations();
        final AssertPerformance assertPerformance = map.get(iterations);
        assertPerformance.consume(message, loopPerformances);
    }
}
