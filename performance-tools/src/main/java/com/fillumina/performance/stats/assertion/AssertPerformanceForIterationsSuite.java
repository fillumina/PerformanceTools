package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Asserts condition about tests executed a specified number of times.
 *
 * @author Francesco Illuminati
 */
//TODO remove this class?
@Deprecated
public class AssertPerformanceForIterationsSuite
        implements PerformanceStatsConsumer,
            Serializable, SuiteIterationsAssertion {
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
            final PerformanceStats stats) {
        //FIXME horrible horrible hack
        final long iterations = stats.getTestPerformances().values().iterator().next().getIterations();
        final AssertPerformance assertPerformance = map.get(iterations);
        assertPerformance.consume(message, stats);
    }
}
