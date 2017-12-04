package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Creates and checks a list of assertions.
 *
 * @param C caller used for fluent interface
 * @param A {@link Assertable} returned
 *
 * @author Francesco Illuminati
 */
public class Assertions
        extends AssertionBuilder<Assertions, Assertions>
        implements Assertion, Serializable {
    private static final long serialVersionUID = 1L;

    private final Collection<Assertion> collection;

    public static Assertions withTolerance(final Ratio tolerance) {
        return new Assertions().setTolerance(tolerance);
    }

    public Assertions() {
        this(new ArrayList<>());
    }

    private Assertions(Collection<Assertion> collection) {
        super(collection::add);
        this.collection = collection;
    }

    @Override
    public Assertions build() {
        return this;
    }

    /**
     * Checks the given {@link Assertable}.
     *
     * @param assertable       the {@link Assertable} to check
     * @throws AssertionError  if the {@link Assertable} doesn't comply
     */
    @Override
    public void accept(Assertable assertable) throws AssertionError {
        collection.forEach(a -> a.accept(assertable) );
    }

    @Override
    public void checkAndReport(Assertable assertable,
            Map<Assertable, List<Assertion>> failedAssertions,
            UnusedAssertionChecker unusedAssertionChecker) {
        collection.forEach(a ->
                a.checkAndReport(assertable, failedAssertions, unusedAssertionChecker) );
    }

    @Override
    public void appendTo(Appendable appendable, Assertable assertable)
            throws IOException {
        for (Assertion assertion : collection) {
            assertion.appendTo(appendable, assertable);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        collection.forEach(a ->
                buf.append(a.toString()).append(System.lineSeparator()) );
        return buf.toString();
    }
}
