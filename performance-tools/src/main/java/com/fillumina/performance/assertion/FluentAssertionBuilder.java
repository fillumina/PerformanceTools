package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.Comparison;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions.
 *
 * @author Francesco Illuminati
 */
public class FluentAssertionBuilder<I extends AssertionBuilder<I,?>, T>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private static class NegatedExperimentAssertionError
            extends ExperimentAssertionError {

        private static final long serialVersionUID = 1L;

        public NegatedExperimentAssertionError(AssertionErrorInfo<?> info) {
            super(info);
        }

        @Override
        public String toString() {
            return "not " + super.toString();
        }
    }

    private class NegatedOrEqualsAssertion
            implements ExperimentAssertion {

        private final InnerAssertion negateAssertion;
        private final ExperimentAssertion equalAssertion;

        // not equals
        public NegatedOrEqualsAssertion(T value) {
            negateAssertion = new InnerAssertion(value, RelativeOrder.EQUALS);
            equalAssertion = ExperimentAssertion.NOK;
        }

        // (less | greater) than or equals
        public NegatedOrEqualsAssertion(T value, RelativeOrder order) {
            negateAssertion = new InnerAssertion(value, order);
            equalAssertion = new InnerAssertion(value, RelativeOrder.EQUALS);
        }

        @Override
        public void check(AssertableExperiment assertable)
                throws AssertionError {
            if (! equalAssertion.satisfy(assertable) &&
                    negateAssertion.satisfy(assertable)) {
                AssertionErrorInfo<?> info = negateAssertion.getInfo(assertable);
                throw new NegatedExperimentAssertionError(info);
            }
        }

        @Override
        public void appendTo(Appendable appendable, AssertableExperiment assExp)
                throws IOException {
            appendable.append("not ");
            negateAssertion.appendTo(appendable, assExp);
        }
    }

    private class InnerAssertion implements ExperimentAssertion {
        private final T value;
        private final RelativeOrder order;

        private AssertionErrorInfo<T> info;
        private AssertableExperiment assertable;

        public InnerAssertion(T value, RelativeOrder order) {
            this.value = value;
            this.order = order;
        }

        public AssertionErrorInfo<T> getInfo(AssertableExperiment assertable) {
            if (assertable.equals(this.assertable)) {
                return info;
            }
            info = infoCreator
                    .create(assertable, testName, order, value, tolerance);
            return info;
        }

        @Override
        public void check(AssertableExperiment assertable)
                throws ExperimentAssertionError {
            getInfo(assertable);
            if (!info.isConditionSatisfied()) {
                throw new ExperimentAssertionError(info);
            }
        }

        @Override
        public void appendTo(Appendable appendable,
                AssertableExperiment assertable)
                throws IOException {
            Measure firstMeasure = assertable.getMeasure(testName);
            new AppendableWrapper(appendable)
                    .print('\'').print(testName).print("' (")
                    .print(firstMeasure).print(") ")
                    .print(satisfy(assertable) ? " is " : "is not ")
                    .print(order.getMessage())
                    .print(' ').print(value)
                    .print(" with a tolerance of ")
                    .print(tolerance);
        }
    }

    private final AssertionBuilder<I,?> assertionBuilder;
    private final AssertionErrorInfoCreator<T> infoCreator;
    private final CharSequence testName;
    private final Ratio tolerance;

    public FluentAssertionBuilder(
            final AssertionBuilder<I,?> assertionBuilder,
            final CharSequence name,
            final Ratio tolerance,
            final AssertionErrorInfoCreator<T> evaluatorCreator) {
        this.assertionBuilder = assertionBuilder;
        this.infoCreator = evaluatorCreator;
        this.testName = name;
        this.tolerance = tolerance;
    }

    /**
     * Accepts a configurable assertion.
     *
     * @param negate    negate the equality constraint
     * @param relativeOrder  constraint
     * @param value     value to use in the evaluation
     * @return self
     */
    public I is(boolean negate, RelativeOrder relativeOrder, T value) {
        Comparison comparison = Comparison.from(relativeOrder, negate);
        return is(comparison, value);
    }

    /**
     * Accepts a configurable assertion.
     *
     * @param comparison constraint
     * @param value      value to use in the evaluation
     * @return self
     */
    public I is(Comparison comparison, T value) {
        switch (comparison) {
            case EQUALS: return equalsTo(value);
            case NOT_EQUALS: return notEqualsTo(value);
            case GREATER: return greaterThan(value);
            case GREATER_OR_EQUALS: return greaterThanOrEquals(value);
            case LESS: return lessThan(value);
            case LESS_OR_EQUALS: return lessThanOrEquals(value);
        }
        throw new AssertionError("unexpected case: " + comparison);
    }

    public I equalsTo(final T value) {
        return createAssertion(value, RelativeOrder.EQUALS);
    }

    public I notEqualsTo(final T value) {
        return createNotEqualAssertion(value);
    }

    public I greaterThan(final T value) {
        return createAssertion(value, RelativeOrder.GREATER);
    }

    public I lessThanOrEquals(final T value) {
        return createNegatedAssertion(value, RelativeOrder.GREATER);
    }

    public I lessThan(final T value) {
        return createAssertion(value, RelativeOrder.LESS);
    }

    public I greaterThanOrEquals(final T value) {
        return createNegatedAssertion(value, RelativeOrder.LESS);
    }

    private I createAssertion(final T value, final RelativeOrder order) {
        final InnerAssertion assertion =
                new InnerAssertion(value, order);
        return assertionBuilder.accept(assertion);
    }

    private I createNegatedAssertion(final T value, final RelativeOrder order) {
        final NegatedOrEqualsAssertion negate =
                new NegatedOrEqualsAssertion(value, order);
        return assertionBuilder.accept(negate);
    }

    private I createNotEqualAssertion(final T value) {
        final NegatedOrEqualsAssertion negate =
                new NegatedOrEqualsAssertion(value);
        return assertionBuilder.accept(negate);
    }
}
