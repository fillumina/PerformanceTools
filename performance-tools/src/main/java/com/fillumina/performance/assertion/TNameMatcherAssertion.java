package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapperSentinel;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.ReentrantImpl;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.TNameMatcher;
import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMatcherAssertion<C, A extends Assertable>
        extends ReentrantImpl<C>
        implements Assertion<A> {

    private interface Evaluator<A extends Assertable> {
        List<Assertion<A>> createAssertions(Collection<TName> names);
    }

    private final List<Evaluator<A>> evaluators = new ArrayList<>();
    private Ratio tolerance = Ratio.percentage(10);

    public TNameMatcherAssertion() {
        super(null);
        setCaller((C)this);
    }

    /**
     *
     * @param caller to allow reentrant fluid interface. see {@link #end()}
     * @param names
     */
    public TNameMatcherAssertion(C caller) {
        super(caller);
    }

    public TNameMatcherAssertion<C, A> withTolerance(final Ratio value) {
        if (value != null) {
            this.tolerance = value;
        }
        return this;
    }

    @Override
    public void check(A assertable) throws AssertionError {
        consume(assertable);
    }

    @Override
    public void consume(A assertable) {
        iterateAssertions(assertable, (assertion) -> {
            try {
                assertion.check(assertable);
            } catch (TestNotFoundException ex) {
                // do nothing
            }
        });
    }

    @Override
    public void toString(final Appendable appendable, final A assertable)
            throws IOException {
        final AppendableWrapperSentinel wrapped =
                new AppendableWrapperSentinel(appendable);

        Holder<Boolean> first = new Holder<>(true);
        try {
            iterateAssertions(assertable, (assertion) -> {
                if (!first.getValue() && wrapped.isModified()) {
                    try {
                        appendable.append(System.lineSeparator());
                        wrapped.setUnmodified();
                        first.setValue(false);
                        try {
                            assertion.toString(wrapped, assertable);
                        } catch (TestNotFoundException ex) {
                            // do nothing
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
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

    private void iterateAssertions(A assertable,
            Consumer<Assertion<A>> consumer) {
        Collection<TName> names = assertable.getTestNames();
        for (Evaluator<A> evaluator : evaluators) {
            List<Assertion<A>> assertions = evaluator.createAssertions(names);
            for (Assertion<A> assertion : assertions) {
                consumer.accept(assertion);
            }
        }
    }

    public TNameMatcher.Builder<OrderCondition> order() {
        return TNameMatcher.builder((builtObject) -> {
            return new OrderCondition(builtObject, tolerance);
        });
    }

    public OrderCondition order(TNameMatcher matcher) {
        return new OrderCondition(matcher, tolerance);
    }

    public class OrderCondition implements Evaluator<A> {
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
                return getCaller();
            });
        }

        public TNameMatcherAssertion<C, A> lessThan(TNameMatcher matcher) {
            this.otherMatcher = matcher;
            equalityCondition = EqCondition.LESS;
            return addToEvaluators(this);
        }

        public TNameMatcherAssertion<C, A> greaterThan(TNameMatcher matcher) {
            this.otherMatcher = matcher;
            equalityCondition = EqCondition.GREATER;
            return addToEvaluators(this);
        }

        public TNameMatcherAssertion<C, A> equalsTo(TNameMatcher matcher) {
            this.otherMatcher = matcher;
            equalityCondition = EqCondition.EQUALS;
            return addToEvaluators(this);
        }

        @Override
        public List<Assertion<A>> createAssertions(Collection<TName> names) {
            List<Assertion<A>> list = new ArrayList<>();
            List<TName> aList = filterNames(names, nameMatcher);
            List<TName> bList = filterNames(names, otherMatcher);
            for (TName aItem : aList) {
                for (TName bItem : bList) {
                    Assertion<A> assertion = AssertStats
                            .<A>withTolerance(tolerance)
                            .assertOrder(aItem)
                            .is(equalityCondition, bItem);
                    list.add(assertion);
                }
            }
            return list;
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

    public class PercentageCondition implements Evaluator<A> {
        private final TNameMatcher nameMatcher;
        private final Ratio tolerance;
        private EqCondition equalityCondition;
        private Ratio percentage;

        public PercentageCondition(TNameMatcher nameMatcher, Ratio tolerance) {
            this.nameMatcher = nameMatcher;
            this.tolerance = new Ratio(tolerance);
        }

        public TNameMatcherAssertion<C, A> lessThan(Ratio percentage) {
            this.percentage = percentage;
            equalityCondition = EqCondition.LESS;
            return addToEvaluators(this);
        }

        public TNameMatcherAssertion<C, A> greaterThan(Ratio percentage) {
            this.percentage = percentage;
            equalityCondition = EqCondition.GREATER;
            return addToEvaluators(this);
        }

        public TNameMatcherAssertion<C, A> equalsTo(Ratio percentage) {
            this.percentage = percentage;
            equalityCondition = EqCondition.EQUALS;
            return addToEvaluators(this);
        }

        @Override
        public List<Assertion<A>> createAssertions(Collection<TName> names) {
            List<TName> aList = filterNames(names, nameMatcher);
            List<Assertion<A>> list = new ArrayList<>();
            for (TName aItem : aList) {
                Assertion<A> assertion = AssertStats
                        .<A>withTolerance(tolerance)
                        .assertPercentage(aItem)
                        .is(equalityCondition, percentage.getPercentage());
                list.add(assertion);
            }
            return list;
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

    public class ValueCondition implements Evaluator<A> {
        private final TNameMatcher nameMatcher;
        private final Ratio tolerance;
        private EqCondition equalityCondition;
        private double value;

        public ValueCondition(TNameMatcher nameMatcher, Ratio tolerance) {
            this.nameMatcher = nameMatcher;
            this.tolerance = new Ratio(tolerance);
        }

        public TNameMatcherAssertion<C, A> lessThan(double value) {
            this.value = value;
            equalityCondition = EqCondition.LESS;
            return addToEvaluators(this);
        }

        public TNameMatcherAssertion<C, A> greaterThan(double value) {
            this.value = value;
            equalityCondition = EqCondition.GREATER;
            return addToEvaluators(this);
        }

        public TNameMatcherAssertion<C, A> equalsTo(double value) {
            this.value = value;
            equalityCondition = EqCondition.EQUALS;
            return addToEvaluators(this);
        }

        @Override
        public List<Assertion<A>> createAssertions(Collection<TName> names) {
            List<Assertion<A>> list = new ArrayList<>();
            List<TName> aList = filterNames(names, nameMatcher);
            for (TName aItem : aList) {
                Assertion<A> assertion = AssertStats
                        .<A>withTolerance(tolerance)
                        .assertValue(aItem)
                        .is(equalityCondition, value);
                list.add(assertion);
            }
            return list;
        }
    }

    private TNameMatcherAssertion<C, A> addToEvaluators(Evaluator<A> evaluator) {
        evaluators.add(evaluator);
        return TNameMatcherAssertion.this;
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
}
