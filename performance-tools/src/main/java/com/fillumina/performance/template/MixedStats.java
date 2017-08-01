package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.formatter.TableFormatter;
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

    public static class Builder {
        private final LinkedMap<Class<? extends Assertable>,
                                AssertableStatsResult.Builder> map =
                new LinkedMap<>();


        @SuppressWarnings("unchecked")
        public AssertableStatsResult.Builder getStatsBuilder(
                Class<? extends Assertable> type) {
            AssertableStatsResult.Builder statsBuilder =  map.get(type);
            if (statsBuilder == null) {
                statsBuilder = AssertableStatsResult.builder();
                map.put(type, statsBuilder);
            }
            return statsBuilder;
        }

        public <C> MixedStats<C> build() {
            return buildWithCallBack(null);
        }

        public <C> MixedStats<C> buildWithCallBack(C callBack) {
            return new MixedStats<>(map.transform((t) -> {
                        return t.buildWithCaller(callBack);
                    }));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Map<Class<? extends Assertable>, AssertableStatsResult<C>> map;

    public MixedStats(
            Map<Class<? extends Assertable>, AssertableStatsResult<C>> map) {
        this.map = map;
    }

    public AssertableStatsResult<C> getStats(Class<? extends Assertable> clazz) {
        return map.get(clazz);
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
            LinkedTree<String,Void> tree = new LinkedTree<>();
            for (AssertableStatsResult<?> statsRes : map.values()) {
                for (TName tn : statsRes.getFlattenedAssertableMap().keySet()) {
                    tree.putValueAtPath(null, tn);
                }
            }
            LinkedMap<TName,Void> map = new LinkedMap<>();
            tree.flatten(map, (list) -> {
                return (TName) list;
            });
            return map.keyList();
        }
    }
}