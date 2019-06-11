package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.assertion.AssertionReport;
import com.fillumina.performance.assertion.ExperimentAssertion;
import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.util.FluentBuilder;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Container for:
 * <ul>
 * <li><b>Statistics</b> resulted form the executions of tests;
 * <li><b>Assertions</b> related to the statistics;
 * <li><b>Viewer</b> viewer for the statistics.
 * </ul>
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionableResult<C>
        extends FluentBuilder<C, AssertionableResult<C>> {

    public static class Builder {
        private StatsHolder statsHolder;
        private List<ExperimentAssertion> assertions;
        private StringGenerator<AssertableExperiment> viewer;

        public Builder addAssertion(ExperimentAssertion assertion) {
            if (assertions == null) {
                assertions = new ArrayList<>();
            }
            assertions.add(assertion);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder setStatsHolder(final StatsHolder value) {
            this.statsHolder = value;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder setStringGenerator(
                final StringGenerator<? extends AssertableExperiment> viewew) {
            this.viewer = (StringGenerator<AssertableExperiment>) viewew;
            return this;
        }

        public <C> AssertionableResult<C> buildWithSetter(
                FluentBuilder.Setter<C, AssertionableResult<C>> setter) {
            return new AssertionableResult<>(
                    setter, statsHolder, assertions, viewer);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final StatsHolder statsHolder;
    private final Collection<ExperimentAssertion> assertions;
    private final StringGenerator<? super AssertableExperiment> viewer;

    private IndexedHashMap<PathName, Stats> flatMap;

    public AssertionableResult(
            FluentBuilder.Setter<C, AssertionableResult<C>> setter,
            StatsHolder statsHolder,
            Collection<ExperimentAssertion> assertions,
            StringGenerator<AssertableExperiment> viewer) {
        super(setter);
        this.statsHolder = statsHolder;
        this.assertions = assertions;
        this.viewer = viewer;
    }

    public StatsHolder getStatsHolder() {
        return statsHolder;
    }

    public void appendFailedAndUnusedAssertions(Appendable appendable) {
        getReport().appendTo(appendable);
    }

    public AssertionReport getReport() {
        if (assertions == null) {
            return AssertionReport.EMPTY;
        }

        AssertionReport report = new AssertionReport();
        report.addAll(assertions, getFlattenedAssertableMap().values());
        return report;
    }

    public void appendNamedTestResults(Appendable appendable, PathName name) {
        AssertableExperiment assertable = getFlattenedAssertableMap().get(name);
        Holder.Boolean assertionsShowed = new Holder.Boolean(false);
        if (assertable != null) {
            viewer.appendToCatchingException(appendable, assertable);
            if (assertions != null && !assertions.isEmpty()) {
                assertions.forEach(assertion -> {
                    if (!assertion.satisfy(assertable)) {
                        append(appendable, "FAILED! ");
                    }
                    try {
                        if (!assertionsShowed.getValue()) {
                            assertionsShowed.setValue(true);
                            newline(appendable);
                        }
                        assertion.appendToCatchingException(appendable,
                                assertable);
                        newline(appendable);
                    } catch (MeasureNotFoundException ex) {
                        // do nothing
                    }
                });
            }
            newline(appendable);
        }
    }

    private void append(Appendable appendable, String message) {
        try {
            appendable.append(message);
        } catch (IOException e) {
            // do nothing
        }
    }

    private void newline(Appendable appendable) {
        try {
            appendable.append(System.lineSeparator());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public IndexedHashMap<PathName, Stats> getFlattenedAssertableMap() {
        if (flatMap == null) {
            if (statsHolder != null && !statsHolder.isEmpty()) {
                flatMap = statsHolder.getFlattenedAssertableMap();
            } else {
                flatMap = IndexedHashMap.emtpy();
            }
        }
        return flatMap;
    }

    @Override
    protected AssertionableResult<C> build() {
        return this;
    }

    // TODO addAll toString()
}
