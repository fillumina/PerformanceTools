package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.tname.TName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ExpressionSolver {

    Map<TName, Measure> solve(Stats stats);

    Map<TName, String> getStringExpressions();
}
