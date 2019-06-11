package com.fillumina.performance.assertion;

import com.fillumina.performance.util.Printable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionReport extends Printable<AssertionReport> {

    public static final AssertionReport EMPTY =
            new AssertionReport(
                    AssertionCatalog.EMPTY,
                    UnusedAssertionChecker.EMPTY);

    private final AssertionCatalog catalog;
    private final UnusedAssertionChecker unused;

    public AssertionReport() {
        this(new AssertionCatalog(), new UnusedAssertionChecker());
    }

    protected AssertionReport(AssertionCatalog catalog,
            UnusedAssertionChecker unused) {
        this.catalog = catalog;
        this.unused = unused;
    }

    public void addAll(Iterable<? extends ExperimentAssertion> assertions,
            Iterable<? extends AssertableExperiment> assertables) {
        for (AssertableExperiment assertable : assertables) {
            assertions.forEach(a -> a.checkAndReport(assertable, this) );
        }
    }

    public void add(ExperimentAssertion assertion,
            AssertableExperiment assertable) {
        try {
            catalog.add(assertion, assertable);
            unused.setUsed(assertion);
        } catch (MeasureNotFoundException e) {
            unused.setUnused(assertion);
        }
    }

    public AssertionReport getUpdateableCatalogOnly() {
        return new AssertionReport(catalog, unused) {
            @Override
            public void add(ExperimentAssertion assertion,
                    AssertableExperiment assertable) {
                try {
                    catalog.add(assertion, assertable);
                } catch (MeasureNotFoundException e) {
                    // do nothing
                }
            }
        };
    }

    public void setUsed(ExperimentAssertion assertion) {
        unused.setUsed(assertion);
    }

    public void setUnused(ExperimentAssertion assertion) {
        unused.setUnused(assertion);
    }

    public boolean isAllSuccessful() {
        return catalog.getFailedAssertions().isEmpty();
    }

    public AssertionCatalog getCatalog() {
        return catalog;
    }

    public UnusedAssertionChecker getUnused() {
        return unused;
    }

    @Override
    public AssertionReport appendTo(Appendable appendable) {
        catalog.appendTo(appendable);
        unused.appendTo(appendable);
        return this;
    }

}
