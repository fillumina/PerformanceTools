package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.param.Option;
import com.fillumina.performance.executor.param.ParameterHelper;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.Printable;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureSum;
import com.fillumina.performance.util.stats.MeasureTimesValue;
import com.fillumina.performance.util.pathname.PathName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Allows to create new statistically accurate {@link Measure}s based on
 * results of actual tests involved in expressions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO put in its own package?
public class StatsExpression<C> extends Printable<StatsExpression<C>>
        implements ExpressionSolver {

    private final C caller;
    private final Map<PathName, ExpressionList<C>> map = new IndexedHashMap<>();
    private Map<PathName, String> stringMap;

    public StatsExpression() {
        this(null);
    }

    public StatsExpression(C caller) {
        this.caller = caller;
    }

    public C end() {
        return caller;
    }

    public ExpressionList<C> addExpression(String... name) {
        return addExpression(PN.pname(name));
    }

    public ExpressionList<C> addExpression(CharSequence name) {
        ExpressionList<C> expressionList = new ExpressionList<>(this, null, false);
        map.put(PN.pname(name), expressionList);
        return expressionList;
    }

    @Override
    public StatsExpression<C> appendTo(Appendable appendable) {
        AppendableWrapper app = new AppendableWrapper(appendable);
        map.forEach((PathName name, ExpressionList<C> expr) -> {
                    app.print(name.toString()).print(": ");
                    expr.appendTo(app);
        });
        return this;
    }

    @Override
    public Map<PathName,String> getStringExpressions() {
        if (stringMap == null || stringMap.size() != map.size()) {
            Map<PathName,String> m = new LinkedHashMap<>();
            map.forEach((PathName name, ExpressionList<C> expr) ->
                    m.put(name, expr.toString()) );
            this.stringMap = Collections.unmodifiableMap(m);
        }
        return stringMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<PathName, Measure> solve(Stats stats) {
        if (stats.isEmpty()) {
            return Collections.<PathName,Measure>emptyMap();
        }

        Map<PathName, Map<String,Option>> optionsMap =
                ParameterHelper.getOptionMap(stats);

        IndexedHashMap<PathName, Measure> measureMap = new IndexedHashMap<>();
        map.forEach((PathName exprName, ExpressionList<C> exp) -> {
                Set<String> paramSet = new HashSet<>();
                exp.addParameters(paramSet);
                if (paramSet.isEmpty()) {
                    measureMap.put(exprName, exp.solve(stats, null, null));
                } else {
                    optionsMap.forEach((PathName pname, Map<String,Option> oc) -> {
                        PathName name = exprName.append(pname);
                        Measure m = exp.solve(stats, oc, name);
                        // TODO changes the names of the expression tests
                        PathName n = exprName.append(pname.getLastName() + "*");
                        measureMap.put(n, m);
                    });
                }
        });
        return measureMap;
    }

    public static class Factors<C> {
        private final ExpressionList<C> parent;
        private List<String> multiplicators = new ArrayList<>();
        private List<String> divisors = new ArrayList<>();

        public Factors(ExpressionList<C> parent) {
            this.parent = parent;
        }

        public Factors<C> multiplyBy(String... array) {
            Arrays.stream(array).forEach(multiplicators::add);
            return this;
        }

        public Factors<C> divideBy(String... array) {
            Arrays.stream(array).forEach(divisors::add);
            return this;
        }

        public ExpressionList<C> end() {
            return parent;
        }

        protected boolean isEmpty() {
            return multiplicators.isEmpty() && divisors.isEmpty();
        }

        protected void addParameters(Set<String> set) {
            set.addAll(multiplicators);
            set.addAll(divisors);
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(multiplicators.stream().collect(Collectors.joining(" * ")));
            if (!divisors.isEmpty()) {
                buf.append(" / (");
                buf.append(divisors.stream().collect(Collectors.joining(" * ")));
                buf.append(") ");
            }
            return buf.toString();
        }

        private double getMultiplicators(Map<String, Option> options) {
            return getFrom(options, multiplicators);
        }

        private Double getDivisors(Map<String, Option> options) {
            return getFrom(options, divisors);
        }

        private double getFrom(Map<String, Option> options, List<String> list) {
            Holder.Double mult = new Holder.Double(1.0);
            list.forEach(s -> {
                Double v = toDouble(options.get(s).getOptionValue());
                if (v != null) {
                    mult.multiply(v);
                }
            });
            return mult.get();
        }

        private Double toDouble(Object obj) {
            if (obj == null) {
                return null;
            }
            return Double.valueOf(obj.toString());
        }
    }

    public static abstract class AbstractExpression<C> {
        protected final ExpressionList<C> parent;
        protected final boolean subtract;
        protected double multiplier = 1.0;
        protected double divisor = 1.0;
        protected Factors<C> factors;

        public AbstractExpression(ExpressionList<C> parent, boolean subtract) {
            this.parent = parent;
            this.subtract = subtract;
        }

        protected abstract Measure solve(Stats stats,
                                        Map<String,Option> optionContainer,
                                        PathName currentTest);

        protected abstract void appendExprTo(AppendableWrapper app);

        protected abstract <E extends AbstractExpression<C>> E addToList(E e);

        public AbstractExpression<C> appendTo(AppendableWrapper app) {
            appendExprTo(app);

            if (factors == null || factors.isEmpty()) {
                if (Double.compare(multiplier, 1.0) != 0) {
                    app.print(" * ").print(multiplier);
                }
                if (Double.compare(divisor, 1.0) != 0) {
                    app.print(" / ").print(divisor);
                }
            } else {
                app.print(" * ").print(factors.toString());
            }
            return this;
        }

        public ExpressionTest<C> addCurrentTest() {
            return addTest(PN.CURRENT);
        }

        public ExpressionTest<C> addTest(String... testName) {
            return addTest(PN.pname(testName));
        }

        public ExpressionTest<C> addTest(CharSequence testName) {
            return addToList(new ExpressionTest<>(getParent(), false,
                    PN.pname(testName) ));
        }

        public ExpressionTest<C> subtractTest(String... testName) {
            return subtractTest(PN.pname(testName));
        }

        public ExpressionTest<C> subtractTest(CharSequence testName) {
            return addToList(new ExpressionTest<>(getParent(), true,
                    PN.pname(testName) ));
        }

        public ExpressionList<C> addExpression() {
            return addToList(new ExpressionList<>(getParent(), false));
        }

        public ExpressionList<C> subtractExpression() {
            return addToList(new ExpressionList<>(getParent(), true));
        }

        public ExpressionList<C> multiplyBy(double value) {
            this.multiplier = value;
            return parent;
        }

        public ExpressionList<C> divideBy(double value) {
            this.divisor = value;
            return parent;
        }

        public Factors<C> factors() {
            if (factors == null) {
                factors = new Factors<>(parent);
            }
            return factors;
        }

        public ExpressionList<C> endExpression() {
            return parent;
        }

        private ExpressionList<C> getParent() {
            if (this instanceof ExpressionList) {
                return (ExpressionList<C>) this;
            }
            return parent;
        }

        protected void addParameters(Set<String> set) {
            if (factors != null && !factors.isEmpty()) {
                factors.addParameters(set);
            }
        }

        protected void copyParameters(Map<String,Option> options) {
            if (options == null || options.isEmpty()) {
                return;
            }
            if (factors != null && !factors.isEmpty()) {
                double mult = factors.getMultiplicators(options);
                if (mult != 1.0) {
                    multiplier = mult;
                }
                double div = factors.getDivisors(options);;
                if (div != 1.0) {
                    divisor = div;
                }
            }
        }
   }

    public static class ExpressionList<C> extends AbstractExpression<C> {
        private final List<AbstractExpression<C>> expressions = new ArrayList<>();
        private StatsExpression<C> caller;

        public ExpressionList(ExpressionList<C> expressionList, boolean subtract) {
            this(null, expressionList, subtract);
        }

        public ExpressionList(StatsExpression<C> caller,
                ExpressionList<C> expressionList, boolean subtract) {
            super(expressionList, subtract);
            this.caller = caller;
        }

        public StatsExpression<C> end() {
            return caller;
        }

        @Override
        public void addParameters(Set<String> paramSet) {
            super.addParameters(paramSet);
            for (AbstractExpression<C> expr : expressions) {
                expr.addParameters(paramSet);
            }
        }

        @Override
        protected <E extends AbstractExpression<C>> E addToList(E e) {
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
            for (AbstractExpression<C> expr : expressions) {
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
        protected Measure solve(Stats stats, Map<String,Option> optionContainer,
                PathName currentTest) {
            Measure measure = null;
            for (AbstractExpression<C> exp : expressions) {
                if (measure == null) {
                    measure = exp.solve(stats, optionContainer, currentTest);
                } else {
                    measure = add(measure,
                            exp.solve(stats, optionContainer, currentTest));
                }
            }
            copyParameters(optionContainer);
            return multiply(measure, (subtract ? -1 : 1) * multiplier / divisor);
        }
    }

    public static class ExpressionTest<C> extends AbstractExpression<C> {
        private final PathName testName;

        public ExpressionTest(ExpressionList<C> parent,
                boolean subtract,
                PathName testName) {
            super(parent, subtract);
            this.testName = testName;
        }

        @Override
        protected <E extends AbstractExpression<C>> E addToList(E e) {
            parent.expressions.add(e);
            return e;
        }

        @Override
        protected void appendExprTo(AppendableWrapper app) {
            app.print("[").print(testName.toString()).print("]");
        }

        @Override
        protected Measure solve(Stats stats, Map<String,Option> optionContainer,
                PathName currentTest) {
            PathName name = PN.CURRENT.equals(testName) ? currentTest : testName;
            Measure measure = stats.getMeasure(name);
            copyParameters(optionContainer);
            return multiply(measure, (subtract ? -1 : 1) * multiplier / divisor);
        }

    }

    private static Measure add(Measure a, Measure b) {
        return new MeasureSum(a, b);
    }

    private static Measure multiply(Measure a, double value) {
        if (value == 1.0) {
            return a;
        }
        return new MeasureTimesValue(a, value);
    }
}
