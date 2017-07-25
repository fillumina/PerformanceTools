package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AddableMultiAssertion;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.infrastructure.AssertableHolder;
import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.Reentrant;
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
public interface AssertableStatsResult<C> extends Reentrant<C> {

    AssertableHolder<?> getStatsHolder();
    Map<Assertable, List<Assertion>> getFailedAssertions();
    void appendFailedAssertions(Appendable appendable);
    void appendNamedTestResults(Appendable appendable, TName name);
    LinkedMap<TName, Assertable> getFlattenedAssertableMap();

    public class AssertableStatsResultImpl<C>
            extends CallBackBuilder<C, AssertableStatsResult<C>>
            implements AssertableStatsResult<C> {

        private AssertableHolder<?> statsHolder;
        private AddableMultiAssertion assertions;
        private AssertableStringGenerator<Assertable> viewer;
        private LinkedMap<TName, Assertable> flatMap;

        public AssertableStatsResultImpl() {
            super();
        }

        public AssertableStatsResultImpl(C caller) {
            super(caller);
        }

        public AssertableStatsResultImpl(Setter<C, AssertableStatsResult<C>> setter) {
            super(setter);
        }

        @SuppressWarnings("unchecked")
        public AssertableStatsResultImpl<C> setStringGenerator(
                AssertableStringGenerator<? extends Assertable> stringGenerator) {
            viewer = (AssertableStringGenerator<Assertable>) stringGenerator;
            return this;
        }

        public AssertableStatsResultImpl<C> setStatsHolder(AssertableHolder<?> stats) {
            this.statsHolder = stats;
            return this;
        }

        public AssertableStatsResultImpl<C> addAssertion(Assertion assertion) {
            if (assertions == null) {
                assertions = new AddableMultiAssertion();
            }
            assertions.addAssertion(assertion);
            return this;
        }

        @Override
        public AssertableHolder<?> getStatsHolder() {
            return statsHolder;
        }

        @Override
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

        @Override
        public Map<Assertable, List<Assertion>> getFailedAssertions() {
            if (assertions == null) {
                return Collections.<Assertable, List<Assertion>>emptyMap();
            }
            Map<Assertable, List<Assertion>> failedAssertions = new LinkedMap<>();
            for (Assertable assertable : getFlattenedAssertableMap().values()) {
                assertions.iterateAssertions(assertable,
                        (Assertion assertion) -> {
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

        @Override
        public void appendNamedTestResults(Appendable appendable, TName name) {
            Assertable assertable = getFlattenedAssertableMap().get(name);
            if (assertable != null) {
                viewer.appendToCatchingException(appendable, assertable);
                newline(appendable);
                if (assertions != null) {
                    assertions.iterateAssertions(assertable,
                            (Assertion assertion) -> {
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

        @Override
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
}
