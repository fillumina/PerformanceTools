package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.stats.Measure;
import java.util.Map;

/**
 * Allows to create expressions that involve actual test results and
 * returns calculated statistically accurate {@link Measure}s.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ExpressionSolver {

    /**
     * @return the result of the managed expressions calculated on the given
     *          {@link Stats}.
     */
    Map<PathName, Measure> solve(Stats stats);

    /** @return a map of string representations of named expressions. */
    Map<PathName, String> getStringExpressions();
}
