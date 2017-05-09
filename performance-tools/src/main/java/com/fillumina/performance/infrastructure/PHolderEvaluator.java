package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.TNameMatcher;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PHolderEvaluator<A extends Assertable>
        implements PerformanceConsumer<A> {
    private final List<TName> names;
    private final List<Assertion<A>> assertions = new ArrayList<>();

    private Ratio tolerance = Ratio.percentage(10);
    private TNameMatcher a;
    private EqCondition equalityCondition;

    public PHolderEvaluator(List<TName> names) {
        this.names = names;
    }

    public PHolderEvaluator<A> withTolerance(final Ratio value) {
        this.tolerance = value;
        return this;
    }

    @Override
    public void consume(A assertable) {
        for (Assertion<A> assertion : assertions) {
            assertion.check(assertable);
        }
    }

    public OrderCondition order(TNameMatcher matcher) {
        a = matcher;
        return new OrderCondition();
    }

    public class OrderCondition {
        private TNameMatcher matcher;

        public PHolderEvaluator<A> lessThan(TNameMatcher matcher) {
            this.matcher = matcher;
            equalityCondition = EqCondition.LESS;
            return evaluate();
        }

        public PHolderEvaluator<A> greaterThan(TNameMatcher matcher) {
            this.matcher = matcher;
            equalityCondition = EqCondition.GREATER;
            return evaluate();
        }

        public PHolderEvaluator<A> equalsTo(TNameMatcher matcher) {
            this.matcher = matcher;
            equalityCondition = EqCondition.EQUALS;
            return evaluate();
        }

        private PHolderEvaluator<A> evaluate() {
            List<TName> aList = filterNames(a);
            List<TName> bList = filterNames(matcher);
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

    public PercentageCondition percentage(TNameMatcher matcher) {
        a = matcher;
        return new PercentageCondition();
    }

    public class PercentageCondition {
        private Ratio percentage;

        public PHolderEvaluator<A> lessThan(Ratio percentage) {
            this.percentage = percentage;
            equalityCondition = EqCondition.LESS;
            return evaluate();
        }

        public PHolderEvaluator<A> greaterThan(Ratio percentage) {
            this.percentage = percentage;
            equalityCondition = EqCondition.GREATER;
            return evaluate();
        }

        public PHolderEvaluator<A> equalsTo(Ratio percentage) {
            this.percentage = percentage;
            equalityCondition = EqCondition.EQUALS;
            return evaluate();
        }

        private PHolderEvaluator<A> evaluate() {
            List<TName> aList = filterNames(a);
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

    public ValueCondition value(TNameMatcher matcher) {
        a = matcher;
        return new ValueCondition();
    }

    public class ValueCondition {
        private double value;

        public PHolderEvaluator<A> lessThan(double value) {
            this.value = value;
            equalityCondition = EqCondition.LESS;
            return evaluate();
        }

        public PHolderEvaluator<A> greaterThan(double value) {
            this.value = value;
            equalityCondition = EqCondition.GREATER;
            return evaluate();
        }

        public PHolderEvaluator<A> equalsTo(double value) {
            this.value = value;
            equalityCondition = EqCondition.EQUALS;
            return evaluate();
        }

        private PHolderEvaluator<A> evaluate() {
            List<TName> aList = filterNames(a);
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
