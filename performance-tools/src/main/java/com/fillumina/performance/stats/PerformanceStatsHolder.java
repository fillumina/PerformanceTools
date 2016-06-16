package com.fillumina.performance.stats;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceStatsHolder
        extends PerformanceHolder<PerformanceStatsHolder, PerformanceStats> {
    private static final long serialVersionUID = 1L;

    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    public static PerformanceStatsHolder empty() {
        return new PerformanceStatsHolder(null);
    }

    public PerformanceStatsHolder(PerformanceStats stats) {
        super(stats);
    }

    public PerformanceStatsHolder(ComposedName name,
            PerformanceStats stats,
            StringGenerator<PerformanceStats> formatter) {
        super(name, stats, formatter);
    }

    /**
     * Check the assertion
     *
     * @see #whenever(boolean)
     * @param assertion to be checked
     * @return {@code this}
     */
    public PerformanceStatsHolder check(
            Assertion<PerformanceStats> assertion) {
        if (isActive()) {
            if (assertion != null) {
                assertion.check(getPerformance());
            }
        }
        return this;
    }
}
