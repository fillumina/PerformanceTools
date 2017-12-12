package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.assertion.UnusedAssertionChecker;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.collection.IndexedArrayMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.tname.TName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        extends CallBackBuilder<C, AssertionableResult<C>> {

    public static class Builder {
        private StatsHolder statsHolder;
        private List<Assertion> assertions;
        private StringGenerator<Assertable> viewer;

        public Builder addAssertion(Assertion assertion) {
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
                final StringGenerator<? extends Assertable> viewew) {
            this.viewer = (StringGenerator<Assertable>) viewew;
            return this;
        }

        public <C> AssertionableResult<C> buildWithSetter(
                CallBackBuilder.Setter<C, AssertionableResult<C>> setter) {
            return new AssertionableResult<>(
                    setter, statsHolder, assertions, viewer);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final StatsHolder statsHolder;
    private final Collection<Assertion> assertions;
    private final StringGenerator<? super Assertable> viewer;

    private IndexedArrayMap<TName, Stats> flatMap;

    public AssertionableResult(
            CallBackBuilder.Setter<C, AssertionableResult<C>> setter,
            StatsHolder statsHolder,
            Collection<Assertion> assertions,
            StringGenerator<Assertable> viewer) {
        super(setter);
        this.statsHolder = statsHolder;
        this.assertions = assertions;
        this.viewer = viewer;
    }

    public StatsHolder getStatsHolder() {
        return statsHolder;
    }

    public void appendFailedAssertions(Appendable appendable) {
        for (Map.Entry<Assertable, List<Assertion>> entry :
                getFailedAssertions().entrySet()) {
            try {
                Assertable assertable = entry.getKey();
                List<Assertion> failedAssertions = entry.getValue();
                for (Assertion a : failedAssertions) {
                    a.appendTo(appendable, assertable);
                }
                appendable.append(System.lineSeparator());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static final Assertable UNCHECKED = new Assertable() {
        @Override public Collection<? extends CharSequence> getNames() {
            return Collections.<CharSequence>emptyList();
        }
        @Override public Measure getMeasure(CharSequence name) { return null; }
        @Override public String toString() { return "UNCHECKED"; }
    };

    public Map<Assertable, List<Assertion>> getFailedAssertions() {
        if (assertions == null) {
            return Collections.<Assertable, List<Assertion>>emptyMap();
        }
        Map<Assertable, List<Assertion>> failedAssertions = new IndexedArrayMap<>();
        UnusedAssertionChecker unusedAssertion = new UnusedAssertionChecker();
        for (Assertable assertable : getFlattenedAssertableMap().values()) {
            assertions.forEach(assertion -> {
                assertion.checkAndReport(assertable, failedAssertions, unusedAssertion);
            });
        }
        List<Assertion> unusedAssertionList = unusedAssertion.getUnusedAssertionList();
        if (!unusedAssertionList.isEmpty()) {
            failedAssertions.put(UNCHECKED, unusedAssertionList);
        }
        return failedAssertions;
    }

    public void appendNamedTestResults(Appendable appendable, TName name) {
        Assertable assertable = getFlattenedAssertableMap().get(name);
        if (assertable != null) {
            viewer.appendToCatchingException(appendable, assertable);
            if (assertions != null && !assertions.isEmpty()) {
                assertions.forEach(assertion -> {
                    try {
                        if (!assertion.satisfy(assertable)) {
                            appendable.append("FAILED! ");
                        }
                    } catch (IOException e) {
                        // do nothing
                    }
                    try {
                        assertion.appendToCatchingException(
                                appendable,
                                assertable);
                        newline(appendable);
                    } catch (MeasureNotFoundException ex) {
                        // do nothing
                    }
                });
                newline(appendable);
            }
        }
    }

    private void newline(Appendable appendable) {
        try {
            appendable.append(System.lineSeparator());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public IndexedArrayMap<TName, Stats> getFlattenedAssertableMap() {
        if (flatMap == null) {
            if (statsHolder != null && !statsHolder.isEmpty()) {
                flatMap = statsHolder.getFlattenedAssertableMap();
            } else {
                flatMap = IndexedArrayMap.emtpy();
            }
        }
        return flatMap;
    }

    @Override
    public AssertionableResult<C> build() {
        return this;
    }

    // TODO add toString()
}
