package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Creates and checks a list of assertions.
 *
 * @param C caller used for fluent interface
 * @param A {@link Assertable} returned
 *
 * @author Francesco Illuminati
 */
public class AssertionChecker
        extends AssertionSelector<AssertionChecker, AssertionChecker>
        implements Assertion, Serializable {
    private static final long serialVersionUID = 1L;

    private static class ConditionsHolder implements AssertionContainer {
        private final List<Assertion> conditions =
                new CopyOnWriteArrayList<>();

        @Override
        public void addAssertion(Assertion assertion) {
            conditions.add(assertion);
        }
    }

    public static AssertionChecker withTolerance(final Ratio tolerance) {
        return new AssertionChecker().tolerance(tolerance);
    }

    public AssertionChecker() {
        super(new ConditionsHolder());
    }

    private List<Assertion> getConditions() {
       return ((ConditionsHolder)getAssertionContainer()).conditions;
    }

    @Override
    public AssertionChecker build() {
        return this;
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void check(Assertable assertable) {
        accept(assertable);
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void accept(Assertable assertable) {
        for (Assertion a: getConditions()) {
            a.accept(assertable);
        }
    }

    @Override
    public void appendTo(Appendable appendable, Assertable assertable)
            throws IOException {
        for (Assertion performanceConsumer : getConditions()) {
            performanceConsumer.appendTo(appendable, assertable);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Assertion a : getConditions()) {
            buf.append(a.toString()).append(System.lineSeparator());
        }
        return buf.toString();
    }
}
