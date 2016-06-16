package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.stats.PerformanceStats;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertSpeedStats extends AssertPerformance<PerformanceStats>
        implements Assertion<PerformanceStats>{
    private static final long serialVersionUID = 1L;

    /** @param tolerance expressed in percentage i.e. 10 means 10 %. */
    public static StatsAssertion<PerformanceStats> withTolerance(
            final double tolerance) {
        return new AssertPerformance<>(new ArrayList<Assertion<PerformanceStats>>())
                .withPercentageTolerance(tolerance);
    }

    protected AssertSpeedStats(List<Assertion<PerformanceStats>> conditions) {
        super(conditions);
    }
}
