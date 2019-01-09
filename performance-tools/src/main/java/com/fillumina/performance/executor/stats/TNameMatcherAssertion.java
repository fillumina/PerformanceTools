package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.assertion.ExperimentAssertion;
import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.assertion.UnusedAssertionChecker;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.AppendableWrapperSentinel;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.CallBackBuilder.Setter;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMatcherAssertion<C> implements ExperimentAssertion {

    public static <C> Builder<C> builder() {
        return new Builder<>();
    }

    public static <C> Builder<C> builder(C caller) {
        return new Builder<>(caller);
    }

    public static <C> Builder<C> builder(Setter<C, ExperimentAssertion> setter) {
        return new Builder<>(setter);
    }

    private final List<Evaluator> evaluators;

    private TNameMatcherAssertion(List<Evaluator> evaluators) {
        this.evaluators = new ArrayList<>(evaluators);
    }

    @Override
    public void accept(AssertableExperiment assertable) {
        forEach(assertable, (assertion) -> {
            try {
                assertion.check(assertable);
            } catch (MeasureNotFoundException ex) {
                // do nothing
            }
        });
    }

    @Override
    public void checkAndReport(AssertableExperiment assertable,
            Map<AssertableExperiment, List<ExperimentAssertion>> failedAssertions,
            UnusedAssertionChecker unusedAssertionChecker) {
        List<TName> tnames = extractFullNames(assertable);
        UnusedAssertionChecker dummy = new UnusedAssertionChecker();
        for (Evaluator evaluator : evaluators) {
            List<ExperimentAssertion> assertions = evaluator.createAssertions(tnames);
            if (assertions.isEmpty()) {
                unusedAssertionChecker.setUnused(evaluator);
            } else {
                unusedAssertionChecker.setUsed(evaluator);
                for (ExperimentAssertion a : assertions) {
                    a.checkAndReport(assertable, failedAssertions, dummy);
                }
            }
        }
    }

    @Override
    public void appendTo(
            final Appendable appendable,
            final AssertableExperiment assertable)
                throws IOException {
        final AppendableWrapperSentinel wrapped =
                new AppendableWrapperSentinel(appendable);

        Holder.Boolean first = new Holder.Boolean(true);
        try {
            forEach(assertable, assertion -> {
                try {
                    if (!first.getValue() && wrapped.isModified()) {
                        appendable.append(System.lineSeparator());
                    }
                    wrapped.setUnmodified();
                    first.setValue(false);
                    try {
                        assertion.appendTo(wrapped, assertable);
                    } catch (MeasureNotFoundException ex) {
                        // do nothing
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (RuntimeException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw ex;
        }
    }

    private void forEach(AssertableExperiment assertable,
            Consumer<ExperimentAssertion> consumer) {
        List<TName> tnames = extractFullNames(assertable);
        for (Evaluator evaluator : evaluators) {
            List<ExperimentAssertion> assertions =
                    evaluator.createAssertions(tnames);
            for (ExperimentAssertion a : assertions) {
                consumer.accept(a);
            }
        }
    }

    private List<TName> extractFullNames(AssertableExperiment assertable) {
        Collection<? extends CharSequence> names = assertable.getNames();
        List<TName> tnames = new ArrayList<>(names.size());
        for (CharSequence cs : names) {
            if (cs instanceof TName) {
                tnames.add((TName) cs);
            } else {
                tnames.add(TN.tname(cs));
            }
        }
        return tnames;
    }

    private interface Evaluator extends ExperimentAssertion {
        List<ExperimentAssertion> createAssertions(Collection<TName> names);

        @Override
        public default void accept(AssertableExperiment t) {
            // do nothing
        }

        @Override
        public default void appendTo(Appendable appendable,
                AssertableExperiment assertable) throws IOException {
            appendable
                    .append("assertion not matching assertables: ")
                    .append(toString())
                    .append(System.lineSeparator());
        }
    }

    public static class Builder<C> extends CallBackBuilder<C, ExperimentAssertion> {
        private final List<Evaluator> evaluators = new ArrayList<>();
        private Ratio tolerance = Ratio.percentage(10);
        private TNameMatcher base = TNameMatcher.EMPTY;

        public Builder() {
            super();
        }

        public Builder(C caller) {
            super(caller);
        }

        public Builder(Setter<C, ExperimentAssertion> setter) {
            super(setter);
        }

        @Override
        public ExperimentAssertion build() {
            return new TNameMatcherAssertion<>(evaluators);
        }

        public Builder<C> tolerance(final Ratio value) {
            if (value != null) {
                this.tolerance = value;
            }
            return this;
        }

        public Builder<C> forTest(String... path) {
            base = path.length == 0 ?
                    TNameMatcher.EMPTY :
                    TNameMatcher.builder().string(path).end();
            return this;
        }

        public TNameMatcher.MatcherBuilder<Builder<C>> with() {
            return TNameMatcher.builder((builtObject) -> {
                base = builtObject;
                return this;
            });
        }

        public OrderCondition order(String... path) {
            TNameMatcher matcher = TNameMatcher.builder().string(path).end();
            return new OrderCondition(base.append(matcher), tolerance);
        }

        public TNameMatcher.MatcherBuilder<OrderCondition> order() {
            return TNameMatcher.builder((builtObject) -> {
                return new OrderCondition(base.append(builtObject), tolerance);
            });
        }

        public OrderCondition order(TNameMatcher matcher) {
            return new OrderCondition(base.append(matcher), tolerance);
        }

        public class OrderCondition implements Evaluator {
            private final Ratio tolerance;
            private final TNameMatcher nameMatcher;
            private TNameMatcher otherMatcher;
            private RelativeOrder equalityCondition;

            public OrderCondition(TNameMatcher nameMatcher, Ratio tolerance) {
                this.nameMatcher = nameMatcher;
                this.tolerance = tolerance;
            }

            public TNameMatcher.MatcherBuilder<C> lessThan() {
                return fluent(RelativeOrder.LESS);
            }

            public TNameMatcher.MatcherBuilder<C> greaterThan() {
                return fluent(RelativeOrder.GREATER);
            }

            public TNameMatcher.MatcherBuilder<C> equalsTo() {
                return fluent(RelativeOrder.EQUALS);
            }

            private TNameMatcher.MatcherBuilder<C> fluent(final RelativeOrder condition) {
                return TNameMatcher.builder((builtObject) -> {
                    otherMatcher = builtObject;
                    equalityCondition = condition;
                    addToEvaluators(this);
                    return Builder.this.end();
                });
            }

            public Builder<C> lessThan(String... str) {
                TNameMatcher matcher = TNameMatcher.builder().string(str).end();
                return lessThan(matcher);
            }

            public Builder<C> greaterThan(String... str) {
                TNameMatcher matcher = TNameMatcher.builder().string(str).end();
                return greaterThan(matcher);
            }

            public Builder<C> equalsTo(String... str) {
                TNameMatcher matcher = TNameMatcher.builder().string(str).end();
                return equalsTo(matcher);
            }

            public Builder<C> lessThan(TNameMatcher matcher) {
                this.otherMatcher = base.append(matcher);
                equalityCondition = RelativeOrder.LESS;
                return addToEvaluators(this);
            }

            public Builder<C> greaterThan(TNameMatcher matcher) {
                this.otherMatcher = base.append(matcher);
                equalityCondition = RelativeOrder.GREATER;
                return addToEvaluators(this);
            }

            public Builder<C> equalsTo(TNameMatcher matcher) {
                this.otherMatcher = base.append(matcher);
                equalityCondition = RelativeOrder.EQUALS;
                return addToEvaluators(this);
            }

            @Override
            public List<ExperimentAssertion> createAssertions(Collection<TName> names) {
                List<ExperimentAssertion> list = new ArrayList<>();
                List<TName> aList = filterNames(names, nameMatcher);
                List<TName> bList = filterNames(names, otherMatcher);
                for (TName aItem : aList) {
                    for (TName bItem : bList) {
                        if (!aItem.equals(bItem)) {
                            ExperimentAssertion assertion = Assertions
                                    .withTolerance(tolerance)
                                    .assertOrder(aItem)
                                    .is(equalityCondition, bItem);
                            list.add(assertion);
                        }
                    }
                }
                return list;
            }

            @Override
            public String toString() {
                return nameMatcher.toString() +
                        " " + equalityCondition.getSymbol() + " " +
                        otherMatcher.toString() +
                        " (" + tolerance.toString() + ")";
            }
        }

        public PercentageCondition percentage(String... path) {
            TNameMatcher matcher = TNameMatcher.builder().string(path).end();
            return new PercentageCondition(base.append(matcher), tolerance);
        }

        public TNameMatcher.MatcherBuilder<PercentageCondition> percentage() {
            return TNameMatcher.builder((builtObject) -> {
                return new PercentageCondition(base.append(builtObject), tolerance);
            });
        }

        public PercentageCondition percentage(TNameMatcher matcher) {
            return new PercentageCondition(base.append(matcher), tolerance);
        }

        public class PercentageCondition implements Evaluator {
            private final TNameMatcher nameMatcher;
            private final Ratio tolerance;
            private RelativeOrder equalityCondition;
            private Ratio percentage;

            public PercentageCondition(TNameMatcher nameMatcher, Ratio tolerance) {
                this.nameMatcher = nameMatcher;
                this.tolerance = tolerance;
            }

            public Builder<C> lessThan(Ratio percentage) {
                this.percentage = percentage;
                equalityCondition = RelativeOrder.LESS;
                return addToEvaluators(this);
            }

            public Builder<C> greaterThan(Ratio percentage) {
                this.percentage = percentage;
                equalityCondition = RelativeOrder.GREATER;
                return addToEvaluators(this);
            }

            public Builder<C> equalsTo(Ratio percentage) {
                this.percentage = percentage;
                equalityCondition = RelativeOrder.EQUALS;
                return addToEvaluators(this);
            }

            @Override
            public List<ExperimentAssertion> createAssertions(Collection<TName> names) {
                List<TName> matchingNames = filterNames(names, nameMatcher);
                List<ExperimentAssertion> list = new ArrayList<>();
                for (TName n : matchingNames) {
                    ExperimentAssertion assertion = Assertions
                            .withTolerance(tolerance)
                            .assertPercentage(n)
                            .is(equalityCondition, percentage.getPercentage());
                    list.add(assertion);
                }
                return list;
            }

            @Override
            public String toString() {
                return nameMatcher.toString() +
                        " " + equalityCondition.getSymbol() + " " +
                        percentage.toString() +
                        " (" + tolerance.toString() + ")";
            }
        }

        public ValueCondition value(String... path) {
            TNameMatcher matcher = TNameMatcher.builder().string(path).end();
            return new ValueCondition(base.append(matcher), tolerance);
        }

        public TNameMatcher.MatcherBuilder<ValueCondition> value() {
            return TNameMatcher.builder((builtObject) -> {
                return new ValueCondition(base.append(builtObject), tolerance);
            });
        }

        public ValueCondition value(TNameMatcher matcher) {
            return new ValueCondition(base.append(matcher), tolerance);
        }

        public class ValueCondition implements Evaluator {
            private final TNameMatcher nameMatcher;
            private final Ratio tolerance;
            private RelativeOrder equalityCondition;
            private double value;

            public ValueCondition(TNameMatcher nameMatcher, Ratio tolerance) {
                this.nameMatcher = nameMatcher;
                this.tolerance = tolerance;
            }

            public Builder<C> lessThan(double value) {
                this.value = value;
                equalityCondition = RelativeOrder.LESS;
                return addToEvaluators(this);
            }

            public Builder<C> greaterThan(double value) {
                this.value = value;
                equalityCondition = RelativeOrder.GREATER;
                return addToEvaluators(this);
            }

            public Builder<C> equalsTo(double value) {
                this.value = value;
                equalityCondition = RelativeOrder.EQUALS;
                return addToEvaluators(this);
            }

            @Override
            public List<ExperimentAssertion> createAssertions(Collection<TName> names) {
                List<ExperimentAssertion> list = new ArrayList<>();
                List<TName> aList = filterNames(names, nameMatcher);
                for (TName aItem : aList) {
                    ExperimentAssertion assertion = Assertions
                            .withTolerance(tolerance)
                            .assertValue(aItem)
                            .is(equalityCondition, value);
                    list.add(assertion);
                }
                return list;
            }

            @Override
            public String toString() {
                return nameMatcher.toString() +
                        " " + equalityCondition.getSymbol() + " " +
                        value +
                        " (" + tolerance.toString() + ")";
            }
        }

        private Builder<C> addToEvaluators(Evaluator evaluator) {
            evaluators.add(evaluator);
            //base = TNameMatcher.EMPTY;
            return Builder.this;
        }

        private static List<TName> filterNames(
                Collection<TName> names, TNameMatcher matcher) {
            List<TName> result = new ArrayList<>();
            for (TName n : names) {
                if (matcher.matches(n)) {
                    result.add(n);
                }
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            for (Evaluator e : evaluators) {
                buf.append(e.toString()).append(System.lineSeparator());
            }
            return buf.toString();
        }
    }
}
