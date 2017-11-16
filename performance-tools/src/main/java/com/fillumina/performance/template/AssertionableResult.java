package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.assertion.UnusedAssertionChecker;
import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.collection.LinkedMap;
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
        private AssertableHolder<Assertable> statsHolder;
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
        public Builder setStatsHolder(
                final AssertableHolder<? extends Assertable> value) {
            this.statsHolder = (AssertableHolder<Assertable>) value;
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

    private final AssertableHolder<Assertable> statsHolder;
    private final Collection<Assertion> assertions;
    private final StringGenerator<? super Assertable> viewer;

    private LinkedMap<TName, Assertable> flatMap;

    public AssertionableResult(
            CallBackBuilder.Setter<C, AssertionableResult<C>> setter,
            AssertableHolder<Assertable> statsHolder,
            Collection<Assertion> assertions,
            StringGenerator<Assertable> viewer) {
        super(setter);
        this.statsHolder = statsHolder;
        this.assertions = assertions;
        this.viewer = viewer;
    }

    public AssertableHolder<?> getStatsHolder() {
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
        Map<Assertable, List<Assertion>> failedAssertions = new LinkedMap<>();
        UnusedAssertionChecker unusedAssertion = new UnusedAssertionChecker();
        for (Assertable assertable : getFlattenedAssertableMap().values()) {
            assertions.forEach(assertion -> {
                assertion.check(assertable, failedAssertions, unusedAssertion);
            });
        }
        List<Assertion> unusedAssertionList =
                unusedAssertion.getFailedAssertions();
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
                    } catch (TestNotFoundException ex) {
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

    public LinkedMap<TName, Assertable> getFlattenedAssertableMap() {
        if (flatMap == null) {
            if (statsHolder != null && !statsHolder.isEmpty()) {
                flatMap = statsHolder.getFlattenedAssertableMap();
            } else {
                flatMap = LinkedMap.<TName, Assertable>empty();
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
