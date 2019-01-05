package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.tname.TName;
import java.util.Map;

/**
 * Allows to create expressions that involves actual tests results and
 * returns calculated statistically accurate {@link Measure}s.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ExpressionSolver {

    /**
     * @return the result of the managed expressions calculated on the given
     *          {@link Stats}.
     */
    Map<TName, Measure> solve(Stats stats);

    /** @return a map of string representations of named expressions. */
    Map<TName, String> getStringExpressions();
}
