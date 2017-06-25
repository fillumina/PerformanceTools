package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.fillumina.performance.infrastructure.AssertableConsumer;

/**
 * Creates and checks a list of assertions.
 *
 * @param C caller used for fluent interface
 * @param A {@link Assertable} returned
 *
 * @author Francesco Illuminati
 */
public class AssertStats<A extends Assertable>
        extends AssertionSelector<AssertStats<A>, AssertStats<A>, A>
        implements Assertion<A>, Serializable {
    private static final long serialVersionUID = 1L;

    private static class ConditionsHolder<A extends Assertable>
            implements AssertionContainer<A> {
        private final List<Assertion<A>> conditions =
                new CopyOnWriteArrayList<>();

        @Override
        public void addAssertion(Assertion<A> assertion) {
            conditions.add(assertion);
        }
    }

    public static <A extends Assertable> AssertStats<A> withTolerance(
            final Ratio tolerance) {
        return new AssertStats<A>().tolerance(tolerance);
    }

    public AssertStats() {
        super(new ConditionsHolder<>());
    }

    private List<Assertion<A>> getConditions() {
       return ((ConditionsHolder<A>)getAssertionContainer()).conditions;
    }

    @Override
    public AssertStats<A> build() {
        return this;
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void check(A assertable) {
        consume(assertable);
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void consume(A assertable) {
        for (AssertableConsumer<A> performanceConsumer: getConditions()) {
            performanceConsumer.consume(assertable);
        }
    }

    @Override
    public void appendTo(Appendable appendable, A assertable)
            throws IOException {
        for (Assertion<A> performanceConsumer : getConditions()) {
            performanceConsumer.appendTo(appendable, assertable);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Assertion<A> a : getConditions()) {
            buf.append(a.toString()).append(System.lineSeparator());
        }
        return buf.toString();
    }
}
