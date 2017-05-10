package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.util.AppendableWrapperSentinel;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.TNameMatcher;
import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PHolderEvaluator<C, A extends Assertable>
        extends ReentrantFluidInterfaceImpl<C>
        implements Assertion<A> {
    private final List<TName> names;
    private final List<Assertion<A>> assertions = new ArrayList<>();

    private Ratio tolerance = Ratio.percentage(10);
    private TNameMatcher nameMatcher;
    private EqCondition equalityCondition;

    public PHolderEvaluator(List<TName> names) {
        this(null, names);
    }

    public PHolderEvaluator(C caller, List<TName> names) {
        super(caller);
        this.names = names;
    }

    public PHolderEvaluator<C, A> withTolerance(final Ratio value) {
        this.tolerance = value;
        return this;
    }

    @Override
    public void check(A assertable) throws AssertionError {
        consume(assertable);
    }

    @Override
    public void consume(A assertable) {
        for (Assertion<A> assertion : assertions) {
            try {
                assertion.check(assertable);
            } catch (TestNotFoundException ex) {
                // do nothing
            }
        }
    }

    @Override
    public void toString(Appendable appendable, A assertable) throws IOException {
        AppendableWrapperSentinel wrapped =
                new AppendableWrapperSentinel(appendable);

        boolean first = true;
        for (Assertion<A> assertion : assertions) {
            if (!first && wrapped.isModified()) {
                appendable.append(System.lineSeparator());
                wrapped.setUnmodified();
                first = false;
            }
            try {
                assertion.toString(wrapped, assertable);
            } catch (TestNotFoundException ex) {
                // do nothing
            }
        }
    }

    public TNameMatcher.Builder<OrderCondition> order() {
        return TNameMatcher.builder((builtObject) -> {
            nameMatcher = builtObject;
            return new OrderCondition();
        });
    }

    public OrderCondition order(TNameMatcher matcher) {
        nameMatcher = matcher;
        return new OrderCondition();
    }

    public class OrderCondition {
        private TNameMatcher otherMatcher;

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
                evaluate();
                return getCaller();
            });
        }

        public PHolderEvaluator<C, A> lessThan(TNameMatcher matcher) {
            this.otherMatcher = matcher;
            equalityCondition = EqCondition.LESS;
            return evaluate();
        }

        public PHolderEvaluator<C, A> greaterThan(TNameMatcher matcher) {
            this.otherMatcher = matcher;
            equalityCondition = EqCondition.GREATER;
            return evaluate();
        }

        public PHolderEvaluator<C, A> equalsTo(TNameMatcher matcher) {
            this.otherMatcher = matcher;
            equalityCondition = EqCondition.EQUALS;
            return evaluate();
        }

        private PHolderEvaluator<C, A> evaluate() {
            List<TName> aList = filterNames(nameMatcher);
            List<TName> bList = filterNames(otherMatcher);
            for (TName aItem : aList) {
                for (TName bItem : bList) {
                    Assertion<A> assertion = AssertStats
                            .<A>withTolerance(tolerance)
                            .assertOrder(aItem)
                            .is(equalityCondition, bItem);
                    assertions.add(assertion);
                }
            }
            return PHolderEvaluator.this;
        }
    }

    public TNameMatcher.Builder<PercentageCondition> percentage() {
        return TNameMatcher.builder((builtObject) -> {
            nameMatcher = builtObject;
            return new PercentageCondition();
        });
    }

    public PercentageCondition percentage(TNameMatcher matcher) {
        nameMatcher = matcher;
        return new PercentageCondition();
    }

    public class PercentageCondition {
        private Ratio percentage;

        public PHolderEvaluator<C, A> lessThan(Ratio percentage) {
            this.percentage = percentage;
            equalityCondition = EqCondition.LESS;
            return evaluate();
        }

        public PHolderEvaluator<C, A> greaterThan(Ratio percentage) {
            this.percentage = percentage;
            equalityCondition = EqCondition.GREATER;
            return evaluate();
        }

        public PHolderEvaluator<C, A> equalsTo(Ratio percentage) {
            this.percentage = percentage;
            equalityCondition = EqCondition.EQUALS;
            return evaluate();
        }

        private PHolderEvaluator<C, A> evaluate() {
            List<TName> aList = filterNames(nameMatcher);
            for (TName aItem : aList) {
                Assertion<A> assertion = AssertStats
                        .<A>withTolerance(tolerance)
                        .assertPercentage(aItem)
                        .is(equalityCondition, percentage.getPercentage());
                assertions.add(assertion);
            }
            return PHolderEvaluator.this;
        }
    }

    public TNameMatcher.Builder<ValueCondition> value() {
        return TNameMatcher.builder((builtObject) -> {
            nameMatcher = builtObject;
            return new ValueCondition();
        });
    }

    public ValueCondition value(TNameMatcher matcher) {
        nameMatcher = matcher;
        return new ValueCondition();
    }

    public class ValueCondition {
        private double value;

        public PHolderEvaluator<C, A> lessThan(double value) {
            this.value = value;
            equalityCondition = EqCondition.LESS;
            return evaluate();
        }

        public PHolderEvaluator<C, A> greaterThan(double value) {
            this.value = value;
            equalityCondition = EqCondition.GREATER;
            return evaluate();
        }

        public PHolderEvaluator<C, A> equalsTo(double value) {
            this.value = value;
            equalityCondition = EqCondition.EQUALS;
            return evaluate();
        }

        private PHolderEvaluator<C, A> evaluate() {
            List<TName> aList = filterNames(nameMatcher);
            for (TName aItem : aList) {
                Assertion<A> assertion = AssertStats
                        .<A>withTolerance(tolerance)
                        .assertValue(aItem)
                        .is(equalityCondition, value);
                assertions.add(assertion);
            }
            return PHolderEvaluator.this;
        }
    }

    private List<TName> filterNames(TNameMatcher matcher) {
        List<TName> result = new ArrayList<>();
        for (TName n : names) {
            if (matcher.matches(n)) {
                result.add(n);
            }
        }
        return result;
    }
}
