package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertOrderCondition<A extends AssertableMultiTest>
        implements Assertion<A>, Serializable {

    private static final long serialVersionUID = 1L;
    private final OrderCondition condition;
    private final String firstTestName;
    private final String secondTestName;
    private final double tolerance;

    public AssertOrderCondition(final OrderCondition condition,
            final String firstTestName, final String secondTestName,
            final double tolerance) {
        this.condition = condition;
        this.firstTestName = firstTestName;
        this.secondTestName = secondTestName;
        this.tolerance = tolerance;
    }

    @Override
    public void check(A assertable) {
        consume(null, assertable);
    }

    @Override
    public void consume(final ComposedName message, final A assertable) {
        if (assertable != null) {
            Measure firstMeasure = getMeasure(assertable, firstTestName);
            Measure secondMeasure = getMeasure(assertable, secondTestName);
            if (!comply(firstMeasure, secondMeasure, tolerance, condition)) {
                throw new OrderAssertionError(message, firstTestName,
                        firstMeasure, secondTestName, secondMeasure, tolerance,
                        condition, assertable);
            }
        }
    }

    private Measure getMeasure(A assertable, String name) {
        Map<String, ? extends AssertableTest> tests =
                assertable.getPerformances();
        if (tests == null) {
            throw new NullPointerException("performances not available");
        }
        final AssertableTest assertableTest = tests.get(name);
        if (assertableTest == null) {
            throw new IllegalStateException("Test '" + name + "' not found, " +
                    "valid tests are: " + tests.keySet().toString());
        }
        return assertableTest.getValue();
    }

    static boolean comply(Measure a, Measure b, final double tolerance,
            OrderCondition condition) throws OrderAssertionError {
        double confidence = (100.0 - tolerance) / 100.0;
        ConfidenceInterval aci = a.getConfidenceInterval(confidence);
        double aLower = aci.getLowerBound();
        double aUpper = aci.getUpperBound();
        ConfidenceInterval bci = b.getConfidenceInterval(confidence);
        double bLower = bci.getLowerBound();
        double bUpper = bci.getUpperBound();
        ConfidenceOrder co = new ConfidenceOrder(tolerance);
        switch (condition) {
            case SAME:
                return (co.gt(bLower, aLower) && co.lt(bUpper, aUpper)) ||
                        (co.gt(bLower, aLower) && co.lt(bLower, aUpper)) ||
                        (co.gt(bUpper, aLower) && co.lt(bUpper, aUpper)) ||
                        (co.lt(bLower, aLower) && co.gt(bUpper, aUpper));
            case SLOWER:
                // bUpper < aLower
                return co.lt(bUpper, aLower);
            case FASTER:
                // aUpper < bLower
                return co.lt(aUpper, bLower);
        }
        throw new AssertionError("condition not managed: " + condition);
    }

    @Override
    public String toString(A assertable) {
        return toString(null, assertable);
    }

    @Override
    public String toString(ComposedName testName, A assertable) {
        StringBuilder buf = new StringBuilder();
        if (testName != null) {
            buf.append(testName).append(System.lineSeparator());
        }
        Measure firstMeasure = getMeasure(assertable, firstTestName);
        Measure secondMeasure = getMeasure(assertable, secondTestName);
        buf.append('\'').append(firstTestName).append("' (").
                append(firstMeasure).append(" ns) ").append(" is ").
                append(condition.getMessage()).append(' ').append('\'').
                append(secondTestName).append("' (").append(secondMeasure).


                //TODO ns and measure printing should be given to a StringGenerator
                append(" ns) ").append(" with a tolerance of ").
                append(tolerance).append(" %").append(System.lineSeparator());
        return buf.toString();
    }

}
