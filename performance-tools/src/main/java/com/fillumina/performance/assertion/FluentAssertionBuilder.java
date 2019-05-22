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

    public interface AssertionErrorCreator<T> {

        AbstractExperimentAssertionError create(
                AssertableExperiment assertable,
                CharSequence testName,
                T value,
                RelativeOrder order,
                Ratio tolerance);
    }

    private class InnerAssertion implements NegableExperimentAssertion {
        private final T value;
        private final RelativeOrder order;

        private AbstractExperimentAssertionError error;
        private AssertableExperiment assertable;

        public InnerAssertion(T value, RelativeOrder order) {
            this.value = value;
            this.order = order;
        }

        @Override
        public AbstractExperimentAssertionError getAssertionError(
                AssertableExperiment assertable) {
            if (assertable.equals(this.assertable)) {
                return error;
            }
            error = errorCreator
                    .create(assertable, testName, value, order, tolerance);
            return error;
        }

        @Override
        public void check(AssertableExperiment assertable)
                throws ExperimentAssertionError {
            if (assertable != null) {
                getAssertionError(assertable)
                    .checkAndThrowExceptionIfNotSatisfied();
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
    private final AssertionErrorCreator<T> errorCreator;
    private final CharSequence testName;
    private final Ratio tolerance;

    public FluentAssertionBuilder(
            final AssertionBuilder<I,?> assertionBuilder,
            final CharSequence name,
            final Ratio tolerance,
            final AssertionErrorCreator<T> errorCreator) {
        this.assertionBuilder = assertionBuilder;
        this.errorCreator = errorCreator;
        this.testName = name;
        this.tolerance = tolerance;
    }

    @Deprecated // TODO remove this, negate use is confusing
    public I is(boolean negate, RelativeOrder equality, T other) {
        if (negate) {
            switch(equality) {
                case EQUALS: return notEqualsTo(other);
                case LESS: return greaterThanOrEquals(other);
                case GREATER: return lessThanOrEquals(other);
            }
        } else {
            switch(equality) {
                case EQUALS: return equalsTo(other);
                case LESS: return lessThan(other);
                case GREATER: return greaterThan(other);
            }
        }
        throw new AssertionError("unexpected case: " + equality);
    }

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
        final InnerAssertion assertion =
                new InnerAssertion(value, RelativeOrder.EQUALS);
        return assertionBuilder.accept(assertion);
    }

    public I notEqualsTo(final T value) {
        final InnerAssertion assertion =
                new InnerAssertion(value, RelativeOrder.EQUALS);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(assertion);
        return assertionBuilder.accept(negate);
    }

    public I greaterThan(final T value) {
        final InnerAssertion assertion =
                new InnerAssertion(value, RelativeOrder.GREATER);
        return assertionBuilder.accept(assertion);
    }

    public I lessThanOrEquals(final T value) {
        final InnerAssertion assertion =
                new InnerAssertion(value, RelativeOrder.GREATER);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(assertion);
        return assertionBuilder.accept(negate);
    }

    public I lessThan(final T value) {
        final InnerAssertion assertion =
                new InnerAssertion(value, RelativeOrder.LESS);
        return assertionBuilder.accept(assertion);
    }

    public I greaterThanOrEquals(final T value) {
        final InnerAssertion assertion =
                new InnerAssertion(value, RelativeOrder.LESS);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(assertion);
        return assertionBuilder.accept(negate);
    }
}
