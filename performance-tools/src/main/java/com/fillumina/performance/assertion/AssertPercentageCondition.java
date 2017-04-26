package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.TreeName;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Asserts if the performance ratio expressed as a percentage of the
 * given test and the  slower one is within the given tolerance.
 * It should be noted that
 * evaluating the performances with ratios between tests rather than with
 * absolute results allows for a much
 * stable, reproducible between different systems and meaningful measures
 * (i.e. expressing the improvement of a new version of an algorithm over
 * a previous version with a ratio is much more useful that stating a
 * timing measurement).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertPercentageCondition<A extends Assertable>
        extends AbstractAssertion<A>
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String testName;
    private final Ratio expectedRatio;
    private final Ratio tolerance;
    private final EqCondition condition;

    AssertPercentageCondition(final String testName,
            final EqCondition condition,
            final Ratio expectedPercentage,
            final Ratio tolerance) {
        this.testName = testName;
        this.condition = condition;
        this.expectedRatio = expectedPercentage;
        this.tolerance = tolerance;
    }

    @Override
    public void consume(final PHolder<A> assertableHolder) {
        if (assertableHolder != null) {
            check(assertableHolder, tolerance);
        }
    }

    public void check(final PHolder<A> assertableHolder,
            final Ratio tolerance) {
        final TreeName title = assertableHolder.getName();
        final Assertable assertable = assertableHolder.getStats();
        Ratio confidence = Ratio.decimal(1 - tolerance.getDecimal());
        if (assertable != null) {
            MeasureRatio actualRatio = assertable
                    .getRatioWithSlowestTest(testName, confidence);

            new PercentageAssertionError(title, testName,
                    actualRatio, expectedRatio, tolerance, condition, assertable)
                    .checkAndThrowExceptionIfNotSatisfied();
        }
    }

    @Override
    public String toString(PHolder<A> assertableHolder) {
        TreeName name = assertableHolder.getName();
        Assertable assertable = assertableHolder.getStats();
        Ratio confidence = Ratio.decimal(1 - tolerance.getDecimal());
        StringBuilder buf = new StringBuilder();
        if (name != null && !name.isEmpty()) {
            buf.append(name).append(':').append(System.lineSeparator());
        }
        buf.append('\'')
                .append(testName)
                .append("' (")
                .append(assertable.getRatioWithSlowestTest(testName, confidence)
                        .toStringAsPercentage())
                .append(") ")
                .append(" is ")
                .append(condition.getMessage())
                .append(' ')
                .append(expectedRatio)
                .append(" with a tolerance of ")
                .append(tolerance);
        return buf.toString();
    }

}
