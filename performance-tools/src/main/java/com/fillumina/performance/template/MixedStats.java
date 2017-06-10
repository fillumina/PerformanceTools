package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AddableMultiAssertion;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Prints speed, used mem and allocated mem results on a per-test basis
 * instead that one after the other.
 *
 * @param S    speed statistics (leaf)
 * @param M    memory statistics (leaf)
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedStats {

    public class SingleStats<A extends Assertable> {
        private StringGenerator<A> viewer;
        private AddableMultiAssertion<A> assertions;
        private PHolder<A> stats;
        private LinkedMap<TName, A> flatMap;

        SingleStats<A> setViewer(StringGenerator<A> viewer) {
            this.viewer = viewer;
            return this;
        }

        SingleStats<A> addAssertion(Assertion<A> assertion) {
            if (assertions == null) {
                assertions = new AddableMultiAssertion<>();
            }
            assertions.addAssertion(assertion);
            return this;
        }

        SingleStats<A> setStats(PHolder<A> stats) {
            this.stats = stats;
            return this;
        }

        public PHolder<A> getStats() {
            return stats;
        }

        public void appendFailedAssertions(Appendable appendable) {
            for (Map.Entry<A, Assertion<A>> entry :
                    getFailedAssertions().entrySet()) {
                try {
                    A assertable = entry.getKey();
                    Assertion<A> assertion = entry.getValue();

                    assertion.appendTo(appendable, assertable);
                    appendable.append(System.lineSeparator());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        public LinkedMap<A, Assertion<A>> getFailedAssertions() {
            if (assertions == null) {
                return LinkedMap.<A,Assertion<A>>empty();
            }
            LinkedMap<A, Assertion<A>> failedAssertions = new LinkedMap<>();
            flatMap = getFlattenedAssertableMap();
            for (A assertable : flatMap.values()) {
                assertions.iterateAssertions(assertable, assertion -> {
                    try {
                        if (!assertion.satisfy(assertable)) {
                            failedAssertions.put(assertable, assertion);
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
                    assertions.iterateAssertions(assertable, assertion -> {
                        try {
                            assertion.appendToCatchingException(
                                    appendable, assertable);
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
                if (stats != null && !stats.isEmpty()) {
                    flatMap = stats.getFlattenedAssertableMap();
                } else {
                    flatMap = LinkedMap.<TName,A>empty();
                }
            }
            return flatMap;
        }
    }

    private final Map<String, SingleStats<?>> map = new HashMap<>();

    public <S extends Assertable> SingleStats<S> getStats(String name) {
        @SuppressWarnings("unchecked")
        SingleStats<S> stats = (SingleStats<S>) map.get(name);
        if (stats == null) {
            stats = new SingleStats<>();
            map.put(name, stats);
        }
        return stats;
    }

    public boolean isSomeAssertionFailed() {
        boolean failed = false;
        for (SingleStats<?> singleStats : map.values()) {
            failed |= !singleStats.getFailedAssertions().isEmpty();
        }
        return failed;
    }

    @Override
    public String toString() {
        return new Appender(new StringBuilder()).appendResults().toString();
    }

    public void appendResultsAndAssertionsTo(Appendable appendable) {
        new Appender(appendable).appendResults();
    }


    public void appendFailedAssertionsTo(Appendable appendable) {
        new Appender(appendable).appendFailedAssertions();
    }

    private class Appender extends AppendableWrapper {

        public Appender(Appendable appendable) {
            super(appendable);
        }

        public Appendable appendResults() {

            List<TName> names = extractNames();

            for (TName name : names) {
                appendTitle(name.toString(), '-');

                for (SingleStats<?> singleStats : map.values()) {
                    singleStats.appendNamedTestResults(getAppendable(), name);
                }
            }

            return getAppendable();
        }

        public Appendable appendFailedAssertions() {
            appendTitle("FAILED ASSERTIONS", '=');

            for (SingleStats<?> singleStats : map.values()) {
                singleStats.appendFailedAssertions(getAppendable());
            }

            return getAppendable();
        }

        private void appendTitle(String title, char underlineChar) {
            print(TableFormatter.title(title, underlineChar));
            newline();
        }

        private List<TName> extractNames() {
            Collection<SingleStats<?>> values = map.values();
            @SuppressWarnings("unchecked")
            List<Set<TName>> list = new ArrayList<>(values.size());
            for (SingleStats<?> ss : values) {
                list.add(ss.getFlattenedAssertableMap().keySet());
            }
            Collections.sort(list, (l1, l2) -> {
                return -Integer.compare(l1.size(), l2.size());
            });
            return new ArrayList<>(list.get(0));
        }
    }

}