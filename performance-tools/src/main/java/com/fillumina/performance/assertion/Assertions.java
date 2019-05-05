package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Builds, holds and checks a collection of assertions.
 *
 * @param C caller used for fluent interface
 * @param A {@link AssertableExperiment} returned
 *
 * @author Francesco Illuminati
 */
public class Assertions
        extends AssertionBuilder<Assertions, Assertions>
        implements ExperimentAssertion, Serializable {
    private static final long serialVersionUID = 1L;

    private final Collection<ExperimentAssertion> collection;

    public static Assertions withTolerance(final Ratio tolerance) {
        return new Assertions().setTolerance(tolerance);
    }

    public Assertions() {
        this(new ArrayList<>());
    }

    private Assertions(Collection<ExperimentAssertion> collection) {
        super(collection::add);
        this.collection = collection;
    }

    @Override
    protected Assertions build() {
        return this;
    }

    /**
     * Checks the given {@link AssertableExperiment}.
     *
     * @param assertable       the {@link AssertableExperiment} to check
     * @throws AssertionError  if the {@link AssertableExperiment} doesn't comply
     */
    @Override
    public void accept(AssertableExperiment assertable) throws AssertionError {
        collection.forEach(a -> a.accept(assertable) );
    }

    @Override
    public void checkAndReport(AssertableExperiment assertable,
            Map<AssertableExperiment, List<ExperimentAssertion>> failedAssertions,
            UnusedAssertionChecker unusedAssertionChecker) {
        collection.forEach(a ->
                a.checkAndReport(assertable, failedAssertions, unusedAssertionChecker) );
    }

    @Override
    public void appendTo(Appendable appendable, AssertableExperiment assertable)
            throws IOException {
        for (ExperimentAssertion assertion : collection) {
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
