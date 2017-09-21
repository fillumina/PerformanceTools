package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapperSentinel;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.CallBackBuilder.Setter;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMatcherAssertion<C> implements Assertion {

    public static <C> Builder<C> builder() {
        return new Builder<>();
    }

    public static <C> Builder<C> builder(C caller) {
        return new Builder<>(caller);
    }

    public static <C> Builder<C> builder(Setter<C, Assertion> setter) {
        return new Builder<>(setter);
    }

    private final List<Evaluator> evaluators;

    private TNameMatcherAssertion(List<Evaluator> evaluators) {
        this.evaluators = new ArrayList<>(evaluators);
    }

    @Override
    public void accept(Assertable assertable) {
        forEach(assertable, (assertion) -> {
            try {
                assertion.check(assertable);
            } catch (TestNotFoundException ex) {
                // do nothing
            }
        });
    }

    @Override
    public void appendTo(
            final Appendable appendable,
            final Assertable assertable)
                throws IOException {
        final AppendableWrapperSentinel wrapped =
                new AppendableWrapperSentinel(appendable);

        Holder<Boolean> first = new Holder<>(true);
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
                    } catch (TestNotFoundException ex) {
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

    private void forEach(Assertable assertable,
            Consumer<Assertion> consumer) {
        Collection<? extends CharSequence> names = assertable.getNames();
        List<TName> tnames = filterTNamesOnly(names);
        for (Evaluator evaluator : evaluators) {
            List<Assertion> assertions = evaluator.createAssertions(tnames);
            for (Assertion a : assertions) {
                consumer.accept(a);
            }
        }
    }

    private List<TName> filterTNamesOnly(
            Collection<? extends CharSequence> names) {
        List<TName> tnames = new ArrayList<>(names.size());
        for (CharSequence cs : names) {
            if (cs instanceof TName) {
                tnames.add((TName) cs);
            }
        }
        return tnames;
    }

    private interface Evaluator {
        List<Assertion> createAssertions(Collection<TName> names);
    }

    public static class Builder<C> extends CallBackBuilder<C, Assertion> {
        private final List<Evaluator> evaluators = new ArrayList<>();
        private Ratio tolerance = Ratio.percentage(10);

        public Builder() {
            super();
        }

        public Builder(C caller) {
            super(caller);
        }

        public Builder(Setter<C, Assertion> setter) {
            super(setter);
        }

        @Override
        public Assertion build() {
            return new TNameMatcherAssertion<>(evaluators);
        }

        public Builder<C> withTolerance(
                final Ratio value) {
            if (value != null) {
                this.tolerance = value;
            }
            return this;
        }

        public TNameMatcher.Builder<OrderCondition> order() {
            return TNameMatcher.builder((builtObject) -> {
                return new OrderCondition(builtObject, tolerance);
            });
        }

        public OrderCondition order(TNameMatcher matcher) {
            return new OrderCondition(matcher, tolerance);
        }

        public class OrderCondition implements Evaluator {
            private final Ratio tolerance;
            private final TNameMatcher nameMatcher;
            private TNameMatcher otherMatcher;
            private EqCondition equalityCondition;

            public OrderCondition(TNameMatcher nameMatcher, Ratio tolerance) {
                this.nameMatcher = nameMatcher;
                this.tolerance = new Ratio(tolerance);
            }

            public TNameMatcher.Builder<C> lessThan() {
                return fluid(EqCondition.LESS);
            }

            public TNameMatcher.Builder<C> greaterThan() {
                return fluid(EqCondition.GREATER);
            }

            public TNameMatcher.Builder<C> equalsTo() {
                return fluid(EqCondition.EQUALS);
            }

            private TNameMatcher.Builder<C> fluid(final EqCondition condition) {
                return TNameMatcher.builder((builtObject) -> {
                    otherMatcher = builtObject;
                    equalityCondition = condition;
                    addToEvaluators(this);
                    return Builder.this.end();
                });
            }

            public Builder<C> lessThan(TNameMatcher matcher) {
                this.otherMatcher = matcher;
                equalityCondition = EqCondition.LESS;
                return addToEvaluators(this);
            }

            public Builder<C> greaterThan(TNameMatcher matcher) {
                this.otherMatcher = matcher;
                equalityCondition = EqCondition.GREATER;
                return addToEvaluators(this);
            }

            public Builder<C> equalsTo(TNameMatcher matcher) {
                this.otherMatcher = matcher;
                equalityCondition = EqCondition.EQUALS;
                return addToEvaluators(this);
            }

            @Override
            public List<Assertion> createAssertions(Collection<TName> names) {
                List<Assertion> list = new ArrayList<>();
                List<TName> aList = filterNames(names, nameMatcher);
                List<TName> bList = filterNames(names, otherMatcher);
                for (TName aItem : aList) {
                    for (TName bItem : bList) {
                        Assertion assertion = Assertions
                                .withTolerance(tolerance)
                                .assertOrder(aItem)
                                .is(equalityCondition, bItem);
                        list.add(assertion);
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

        public TNameMatcher.Builder<PercentageCondition> percentage() {
            return TNameMatcher.builder((builtObject) -> {
                return new PercentageCondition(builtObject, tolerance);
            });
        }

        public PercentageCondition percentage(TNameMatcher matcher) {
            return new PercentageCondition(matcher, tolerance);
        }

        public class PercentageCondition implements Evaluator {
            private final TNameMatcher nameMatcher;
            private final Ratio tolerance;
            private EqCondition equalityCondition;
            private Ratio percentage;

            public PercentageCondition(TNameMatcher nameMatcher, Ratio tolerance) {
                this.nameMatcher = nameMatcher;
                this.tolerance = new Ratio(tolerance);
            }

            public Builder<C> lessThan(Ratio percentage) {
                this.percentage = percentage;
                equalityCondition = EqCondition.LESS;
                return addToEvaluators(this);
            }

            public Builder<C> greaterThan(Ratio percentage) {
                this.percentage = percentage;
                equalityCondition = EqCondition.GREATER;
                return addToEvaluators(this);
            }

            public Builder<C> equalsTo(Ratio percentage) {
                this.percentage = percentage;
                equalityCondition = EqCondition.EQUALS;
                return addToEvaluators(this);
            }

            @Override
            public List<Assertion> createAssertions(Collection<TName> names) {
                List<TName> matchingNames = filterNames(names, nameMatcher);
                List<Assertion> list = new ArrayList<>();
                for (TName n : matchingNames) {
                    Assertion assertion = Assertions
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

        public TNameMatcher.Builder<ValueCondition> value() {
            return TNameMatcher.builder((builtObject) -> {
                return new ValueCondition(builtObject, tolerance);
            });
        }

        public ValueCondition value(TNameMatcher matcher) {
            return new ValueCondition(matcher, tolerance);
        }

        public class ValueCondition implements Evaluator {
            private final TNameMatcher nameMatcher;
            private final Ratio tolerance;
            private EqCondition equalityCondition;
            private double value;

            public ValueCondition(TNameMatcher nameMatcher, Ratio tolerance) {
                this.nameMatcher = nameMatcher;
                this.tolerance = new Ratio(tolerance);
            }

            public Builder<C> lessThan(double value) {
                this.value = value;
                equalityCondition = EqCondition.LESS;
                return addToEvaluators(this);
            }

            public Builder<C> greaterThan(double value) {
                this.value = value;
                equalityCondition = EqCondition.GREATER;
                return addToEvaluators(this);
            }

            public Builder<C> equalsTo(double value) {
                this.value = value;
                equalityCondition = EqCondition.EQUALS;
                return addToEvaluators(this);
            }

            @Override
            public List<Assertion> createAssertions(Collection<TName> names) {
                List<Assertion> list = new ArrayList<>();
                List<TName> aList = filterNames(names, nameMatcher);
                for (TName aItem : aList) {
                    Assertion assertion = Assertions
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
