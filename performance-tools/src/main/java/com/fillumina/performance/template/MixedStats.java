package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Prints speed, used mem and allocated mem results on a per-test basis
 * instead that one after the other.
 *
 * @param S    speed statistics (leaf)
 * @param M    memory statistics (leaf)
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedStats<C> {

    private final Map<Class<? extends Assertable>, AssertableStatsResult<C>> map =
            new HashMap<>();

    private C callBack;

    void setCallBack(C callBack) {
        this.callBack = callBack;
    }

    @SuppressWarnings("unchecked")
    public AssertableStatsResult<C> getStats(Class<? extends Assertable> type) {
        @SuppressWarnings("unchecked")
        AssertableStatsResult<C> stats = map.get(type);
        if (stats == null) {
            stats = new AssertableStatsResult<>(
                    (builtObject) -> {return callBack;} );
            map.put(type, stats);
        }
        return stats;
    }

    public boolean isSomeAssertionFailed() {
        boolean failed = false;
        for (AssertableStatsResult<C> singleStats : map.values()) {
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

                for (AssertableStatsResult<?> singleStats : map.values()) {
                    singleStats.appendNamedTestResults(getAppendable(), name);
                }
            }

            return getAppendable();
        }

        public Appendable appendFailedAssertions() {
            appendTitle("FAILED ASSERTIONS", '=');

            for (AssertableStatsResult<?> singleStats : map.values()) {
                singleStats.appendFailedAssertions(getAppendable());
            }

            return getAppendable();
        }

        private void appendTitle(String title, char underlineChar) {
            print(TableFormatter.title(title, underlineChar));
            newline();
        }

        private List<TName> extractNames() {
            List<TName> list = new ArrayList<>();
            int lastIndex = 0;
            for (AssertableStatsResult<?> statsRes : map.values()) {
                for (TName tn : statsRes.getFlattenedAssertableMap().keySet()) {
                    int index = list.indexOf(tn);
                    if (index == -1 || lastIndex == list.size()) {
                        list.add(tn);
                    } else {
                        list.add(lastIndex + 1, tn);
                    }
                    lastIndex = index;
                }
            }
            return list;
        }
    }

}