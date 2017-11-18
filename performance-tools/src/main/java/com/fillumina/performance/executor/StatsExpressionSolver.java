package com.fillumina.performance.executor;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsExpressionSolver {

    public final Map<TName, ExpressionList> map = new LinkedMap<>();

    public ExpressionList createExpression(TName name) {
        ExpressionList expressionList = new ExpressionList(null, false);
        map.put(name, expressionList);
        return expressionList;
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleStats> Stats<T> solve(Stats<T> stats) {
        if (stats.isEmpty()) {
            return stats;
        }
        LinkedMap<TName, Measure> measureMap = new LinkedMap<>();
        map.forEach((TName name, ExpressionList exp) ->
            measureMap.put(name, exp.solve(stats)) );
        return stats.createNewAdding(measureMap);
    }

    public abstract class AbstractExpression {
        protected final ExpressionList expressionList;
        private final boolean subtract;

        public AbstractExpression(ExpressionList expressionList, boolean subtract) {
            this.expressionList = expressionList;
            this.subtract = subtract;
        }

        abstract Measure solve(Stats<?> assertable);

        public ExpressionTest addTest(TName testName) {
            return addToList(new ExpressionTest(expressionList, false, testName));
        }

        public ExpressionTest subtractTest(TName testName) {
            return addToList(new ExpressionTest(expressionList, true, testName));
        }

        public ExpressionList addExpression() {
            return addToList(new ExpressionList(expressionList, false));
        }

        public ExpressionList subtractExpression() {
            return addToList(new ExpressionList(expressionList, true));
        }

        private <E extends AbstractExpression> E addToList(E e) {
            expressionList.expressions.add(e);
            return e;
        }
    }

    public static class ExpressionList extends AbstractExpression {
        private final List<AbstractExpression> expressions = new ArrayList<>();

        public ExpressionList(ExpressionList expressionList, boolean subtract) {
            super(expressionList, subtract);
        }

        public ExpressionList endExpression() {
            return expressionList;
        }

        @Override
        Measure solve(Stats<?> stats) {
            Assertable current;
            for (AbstractExpression exp : expressions) {

            }
            return null;
        }
    }

    public static class ExpressionTest extends AbstractExpression {
        private final TName testName;

        private double multiplier;

        public ExpressionTest(ExpressionList expressionList,
                boolean subtract,
                TName testName) {
            super(expressionList, subtract);
            this.testName = testName;
        }

        public ExpressionTest multiplyBy(double value) {
            this.multiplier = value;
            return this;
        }

        public ExpressionTest divideBy(double value) {
            this.multiplier = 1.0 / value;
            return this;
        }

        @Override
        Measure solve(Stats<?> stats) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private static Assertable add(Assertable a, Assertable b, boolean subtract) {
        return null;
    }

    private Assertable multiply(Assertable a, double value) {
        return null;
    }
}
