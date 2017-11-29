package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.Printable;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureSum;
import com.fillumina.performance.util.stats.MeasureTimesValue;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsExpression<C> extends Printable<StatsExpression<C>>
        implements ExpressionSolver {

    private final C caller;
    private final Map<TName, ExpressionList> map = new LinkedMap<>();
    private Map<TName,String> stringMap;

    public StatsExpression() {
        this(null);
    }

    public StatsExpression(C caller) {
        this.caller = caller;
    }

    public C end() {
        return caller;
    }

    public ExpressionList addExpression(String... name) {
        return addExpression(TN.tname(name));
    }

    public ExpressionList addExpression(CharSequence name) {
        ExpressionList expressionList = new ExpressionList(null, false);
        map.put(TN.tname(name), expressionList);
        return expressionList;
    }

    @Override
    public StatsExpression<C> appendTo(Appendable appendable) {
        AppendableWrapper app = new AppendableWrapper(appendable);
        map.forEach((TName name, ExpressionList expr) -> {
                    app.print(name.toString()).print(": ");
                    expr.appendTo(app);
        });
        return this;
    }

    @Override
    public Map<TName,String> getStringExpressions() {
        if (stringMap == null || stringMap.size() != map.size()) {
            Map<TName,String> m = new LinkedHashMap<>();
            map.forEach( (TName name, ExpressionList expr) ->
                    m.put(name, expr.toString()) );
            this.stringMap = Collections.unmodifiableMap(m);
        }
        return stringMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<TName, Measure> solve(Stats stats) {
        if (stats.isEmpty()) {
            return Collections.<TName,Measure>emptyMap();
        }
        LinkedMap<TName, Measure> measureMap = new LinkedMap<>();
        map.forEach((TName name, ExpressionList exp) ->
            measureMap.put(name, exp.solve(stats)) );
        return measureMap;
    }

    public static abstract class AbstractExpression {
        protected final ExpressionList parent;
        protected final boolean subtract;
        protected double multiplier = 1.0;
        protected double divisor = 1.0;

        public AbstractExpression(ExpressionList parent, boolean subtract) {
            this.parent = parent;
            this.subtract = subtract;
        }

        protected abstract Measure solve(Stats stats);

        protected abstract void appendExprTo(AppendableWrapper app);

        protected abstract <E extends AbstractExpression> E addToList(E e);

        public AbstractExpression appendTo(AppendableWrapper app) {
            appendExprTo(app);
            if (Double.compare(multiplier, 1.0) != 0) {
                app.print(" * ").print(multiplier);
            }
            if (Double.compare(divisor, 1.0) != 0) {
                app.print(" / ").print(divisor);
            }
            return this;
        }

        public ExpressionTest addTest(String... testName) {
            return addTest(TN.tname(testName));
        }

        public ExpressionTest addTest(CharSequence testName) {
            return addToList(new ExpressionTest(getParent(), false,
                    TN.tname(testName) ));
        }

        public ExpressionTest subtractTest(String... testName) {
            return subtractTest(TN.tname(testName));
        }

        public ExpressionTest subtractTest(CharSequence testName) {
            return addToList(new ExpressionTest(getParent(), true,
                    TN.tname(testName) ));
        }

        public ExpressionList addExpression() {
            return addToList(new ExpressionList(getParent(), false));
        }

        public ExpressionList subtractExpression() {
            return addToList(new ExpressionList(getParent(), true));
        }

        public ExpressionList multiplyBy(double value) {
            this.multiplier = value;
            return parent;
        }

        public ExpressionList divideBy(double value) {
            this.divisor = value;
            return parent;
        }

        public ExpressionList endExpression() {
            return parent;
        }

        private ExpressionList getParent() {
            if (this instanceof ExpressionList) {
                return (ExpressionList) this;
            }
            return parent;
        }
   }

    public static class ExpressionList extends AbstractExpression {
        private final List<AbstractExpression> expressions = new ArrayList<>();

        public ExpressionList(ExpressionList expressionList, boolean subtract) {
            super(expressionList, subtract);
        }

        @Override
        protected <E extends AbstractExpression> E addToList(E e) {
            expressions.add(e);
            return e;
        }

        @Override
        public String toString() {
            AppendableWrapper app = new AppendableWrapper(new StringBuilder());
            appendExprTo(app);
            return app.toString();
        }

        @Override
        protected void appendExprTo(AppendableWrapper app) {
            if (parent != null) {
                app.print("(");
            }
            boolean first = true;
            for (AbstractExpression expr : expressions) {
                if (expr.subtract) {
                    app.print(" - ");
                } else if (!first) {
                    app.print(" + ");
                }
                expr.appendTo(app);
                if (first) {
                    first = false;
                }
            }
            if (parent != null) {
                app.print(")");
            }
        }

        @Override
        protected Measure solve(Stats stats) {
            Measure measure = null;
            for (AbstractExpression exp : expressions) {
                if (measure == null) {
                    measure = exp.solve(stats);
                } else {
                    measure = add(measure, exp.solve(stats));
                }
            }
            return multiply(measure, (subtract ? -1 : 1) * multiplier / divisor);
        }
    }

    public static class ExpressionTest extends AbstractExpression {
        private final TName testName;

        public ExpressionTest(ExpressionList parent,
                boolean subtract,
                TName testName) {
            super(parent, subtract);
            this.testName = testName;
        }

        @Override
        protected <E extends AbstractExpression> E addToList(E e) {
            parent.expressions.add(e);
            return e;
        }

        @Override
        protected void appendExprTo(AppendableWrapper app) {
            app.print("[").print(testName.toString()).print("]");
        }

        @Override
        protected Measure solve(Stats stats) {
            Measure measure = stats.getMeasure(testName);
            return multiply(measure, (subtract ? -1 : 1) * multiplier / divisor);
        }

    }

    private static Measure add(Measure a, Measure b) {
        return new MeasureSum(a, b);
    }

    private static Measure multiply(Measure a, double value) {
        return new MeasureTimesValue(a, value);
    }
}
