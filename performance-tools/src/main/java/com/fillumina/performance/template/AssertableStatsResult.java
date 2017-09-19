package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AddableMultiAssertion;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.infrastructure.AssertableHolder;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.tname.TName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableStatsResult<C>
        extends CallBackBuilder<C, AssertableStatsResult<C>> {

    public static class Builder {
        private AssertableHolder<Assertable> statsHolder;
        private AddableMultiAssertion assertions;
        private StringGenerator<Assertable> viewer;

        public Builder addAssertion(Assertion assertion) {
            if (assertions == null) {
                assertions = new AddableMultiAssertion();
            }
            assertions.addAssertion(assertion);
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

        public <C> AssertableStatsResult<C> buildWithSetter(
                CallBackBuilder.Setter<C, AssertableStatsResult<C>> setter) {
            return new AssertableStatsResult<>(
                    setter, statsHolder, assertions, viewer);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final AssertableHolder<Assertable> statsHolder;
    private final Iterable<Assertion> assertions;
    private final StringGenerator<? super Assertable> viewer;
    private LinkedMap<TName, Assertable> flatMap;

    public AssertableStatsResult(
            CallBackBuilder.Setter<C, AssertableStatsResult<C>> setter,
            AssertableHolder<Assertable> statsHolder,
            Iterable<Assertion> assertions,
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

    public Map<Assertable, List<Assertion>> getFailedAssertions() {
        if (assertions == null) {
            return Collections.<Assertable, List<Assertion>>emptyMap();
        }
        Map<Assertable, List<Assertion>> failedAssertions = new LinkedMap<>();
        for (Assertable assertable : getFlattenedAssertableMap().values()) {
            assertions.forEach(assertion -> {
                try {
                    if (!assertion.satisfy(assertable)) {
                        List<Assertion> list =
                                failedAssertions.get(assertable);
                        if (list == null) {
                            list = new ArrayList<>();
                            failedAssertions.put(assertable, list);
                        }
                        list.add(assertion);
                    }
                } catch (TestNotFoundException e) {
                    // do nothing
                }
            });
        }
        return failedAssertions;
    }

    public void appendNamedTestResults(Appendable appendable, TName name) {
        Assertable assertable = getFlattenedAssertableMap().get(name);
        if (assertable != null) {
            viewer.appendToCatchingException(appendable, assertable);
            newline(appendable);
            if (assertions != null) {
                assertions.forEach(assertion -> {
                    try {
                        assertion.appendToCatchingException(
                                appendable,
                                assertable);
                    } catch (TestNotFoundException ex) {
                        // do nothing
                    }
                    newline(appendable);
                });
                newline(appendable);
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
    public AssertableStatsResult<C> build() {
        return this;
    }
}
