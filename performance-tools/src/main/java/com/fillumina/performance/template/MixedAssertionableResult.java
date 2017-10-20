package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.tname.TName;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertionableResult<C> {

    private static class CallBackSetter<C>
            implements CallBackBuilder.Setter<C, AssertionableResult<C>> {

        private C callBack;

        void setCallBack(C callBack) {
            this.callBack = callBack;
        }

        @Override
        public C setBuiltObjectAndReturn(AssertionableResult<C> builtObject) {
            return callBack;
        }

    }

    public static class Builder {
        private final LinkedMap<Class<? extends Assertable>,
                                AssertionableResult.Builder> map =
                new LinkedMap<>();

        @SuppressWarnings("unchecked")
        public AssertionableResult.Builder getStatsBuilder(
                Class<? extends Assertable> type) {
            AssertionableResult.Builder statsBuilder =  map.get(type);
            if (statsBuilder == null) {
                statsBuilder = AssertionableResult.builder();
                map.put(type, statsBuilder);
            }
            return statsBuilder;
        }

        public <C> MixedAssertionableResult<C> build() {
            CallBackSetter<C> setter = new CallBackSetter<>();
            return new MixedAssertionableResult<>(map.transform(
                    (AssertionableResult.Builder builder) -> {
                        return builder.buildWithSetter(setter);
                    }), setter);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Map<Class<? extends Assertable>, AssertionableResult<C>> map;
    private final CallBackSetter<C> setter;

    private MixedAssertionableResult(
            Map<Class<? extends Assertable>, AssertionableResult<C>> map,
            CallBackSetter<C> setter) {
        this.map = Collections.unmodifiableMap(map);
        this.setter = setter;
    }

    void setCallBack(C callBack) {
        setter.setCallBack(callBack);
    }

    public AssertionableResult<C> getStats(Class<? extends Assertable> clazz) {
        return map.get(clazz);
    }

    public boolean isSomeAssertionFailed() {
        boolean failed = false;
        for (AssertionableResult<C> singleStats : map.values()) {
            failed |= !singleStats.getFailedAssertions().isEmpty();
        }
        return failed;
    }

    @Override
    public String toString() {
        return new Appender(new StringBuilder()).appendResults().toString();
    }

    public Map<Class<? extends Assertable>, AssertionableResult<C>> getMap() {
        return map;
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

                for (AssertionableResult<?> singleStats : map.values()) {
                    singleStats.appendNamedTestResults(getAppendable(), name);
                }
            }

            return getAppendable();
        }

        public Appendable appendFailedAssertions() {
            appendTitle("FAILED ASSERTIONS", '=');

            for (AssertionableResult<?> singleStats : map.values()) {
                singleStats.appendFailedAssertions(getAppendable());
            }

            return getAppendable();
        }

        private void appendTitle(String title, char underlineChar) {
            if (title != null) {
                print(TableFormatter.title(title, underlineChar));
                newline();
            }
        }

        private List<TName> extractNames() {
            LinkedTree<String,Void> tree = new LinkedTree<>();
            TName last = null;
            for (AssertionableResult<?> statsRes : map.values()) {
                for (TName tn : statsRes.getFlattenedAssertableMap().keySet()) {
                    last = tn;
                    tree.putValueAtPath(null, tn);
                }
            }
            if (tree.isEmpty()) {
                return Collections.singletonList(last);
            }
            LinkedMap<TName,Void> map = new LinkedMap<>();
            tree.flatten(map, list -> TN.tname(list) );
            return map.keyList();
        }
    }
}