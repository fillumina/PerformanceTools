package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AddableMultiAssertion;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableStatsResult<C, A extends Assertable>
        extends CallBackBuilder<C, AssertableStatsResult<C,A>> {

    private StringGenerator<A> viewer;
    private AddableMultiAssertion<A> assertions;
    private PHolder<A> statsHolder;
    private LinkedMap<TName, A> flatMap;

    public AssertableStatsResult() {
    }

    public AssertableStatsResult(C caller) {
        super(caller);
    }

    public AssertableStatsResult(
            Setter<C, AssertableStatsResult<C, A>> setter) {
        super(setter);
    }

    AssertableStatsResult<C,A> setViewer(StringGenerator<A> viewer) {
        this.viewer = viewer;
        return this;
    }

    AssertableStatsResult<C,A> addAssertion(Assertion<A> assertion) {
        if (assertions == null) {
            assertions = new AddableMultiAssertion<>();
        }
        assertions.addAssertion(assertion);
        return this;
    }

    AssertableStatsResult<C,A> setStatsHolder(PHolder<A> stats) {
        this.statsHolder = stats;
        return this;
    }

    public PHolder<A> getStatsHolder() {
        return statsHolder;
    }

    public void appendFailedAssertions(Appendable appendable) {
        for (Map.Entry<A, List<Assertion<A>>> entry : getFailedAssertions().
                entrySet()) {
            try {
                A assertable = entry.getKey();
                List<Assertion<A>> assertions = entry.getValue();
                for (Assertion<A> a : assertions) {
                    a.appendTo(appendable, assertable);
                }
                appendable.append(System.lineSeparator());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public Map<A, List<Assertion<A>>> getFailedAssertions() {
        if (assertions == null) {
            return Collections.<A, List<Assertion<A>>>emptyMap();
        }
        Map<A, List<Assertion<A>>> failedAssertions = new LinkedMap<>();
        flatMap = getFlattenedAssertableMap();
        for (A assertable : flatMap.values()) {
            assertions.iterateAssertions(assertable,
                    (com.fillumina.performance.assertion.Assertion<A> assertion) -> {
                try {
                    if (!assertion.satisfy(assertable)) {
                        List<Assertion<A>> list =
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

    public void appendNamedTestResults(Appendable appendable, TName n) {
        A assertable = getFlattenedAssertableMap().get(n);
        if (assertable != null) {
            viewer.appendToCatchingException(appendable, assertable);
            newline(appendable);
            if (assertions != null) {
                assertions.iterateAssertions(assertable,
                        (Assertion<A> assertion) -> {
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

    public LinkedMap<TName, A> getFlattenedAssertableMap() {
        if (flatMap == null) {
            if (statsHolder != null && !statsHolder.isEmpty()) {
                flatMap = statsHolder.getFlattenedAssertableMap();
            } else {
                flatMap = LinkedMap.<TName, A>empty();
            }
        }
        return flatMap;
    }

    @Override
    public AssertableStatsResult<C, A> build() {
        return this;
    }
}
