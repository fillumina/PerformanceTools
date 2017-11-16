package com.fillumina.performance.util.tname;

import com.fillumina.performance.util.CallBackBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMatcher {
    public static final TNameMatcher EMPTY =
            new TNameMatcher(Collections.<Condition>emptyList());

    private final List<Condition> conditions;

    public static class Builder<C> extends CallBackBuilder<C,TNameMatcher> {
        private final List<Condition> conditions = new ArrayList<>();

        private Builder() {
            super();
        }

        private Builder(Setter<C, TNameMatcher> setter) {
            super(setter);
        }

        /** Matches a fixed string. */
        public Builder<C> string(String... names) {
            for (String n : names) {
                conditions.add(new FixedCondition(n));
            }
            return this;
        }

        /** Matches numbers less than the given one. */
        public Builder<C> lessThan(double value) {
            conditions.add(new ComparatorCondition(value, -1));
            return this;
        }

        /** Matches numbers greater than the given one. */
        public Builder<C> greaterThan(double value) {
            conditions.add(new ComparatorCondition(value, 1));
            return this;
        }

        /** Matches numbers equals to the given one. */
        public Builder<C> equalsTo(double value) {
            conditions.add(new ComparatorCondition(value, 0));
            return this;
        }

        /** Matches numbers between the given interval (inclusive). */
        public Builder<C> interval(double from, double to) {
            conditions.add(new IntervalCondition(from, to));
            return this;
        }

        /** Matches the given REGEXP pattern. */
        public Builder<C> pattern(String pattern) {
            conditions.add(new RegexpCondition(pattern));
            return this;
        }

        /** Matches a single name (same as .? in REGEXP). */
        public Builder<C> jolly() {
            conditions.add(JOLLY);
            return this;
        }

        /** Matches zero or more names (same as .* in REGEXP). */
        public Builder<C> all() {
            conditions.add(ALL);
            return this;
        }

        /** Adds a user defined conditions. */
        public Builder<C> condition(Condition matcherNode) {
            conditions.add(matcherNode);
            return this;
        }

        @Override
        public TNameMatcher build() {
            return new TNameMatcher(conditions);
        }
    }

    public static Builder<TNameMatcher> builder() {
        return new Builder<>();
    }

    public static <C> Builder<C> builder(
            CallBackBuilder.Setter<C,TNameMatcher> setter) {
        return new Builder<>(setter);
    }

    private TNameMatcher(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public TNameMatcher append(TNameMatcher other) {
        List<Condition> list =
                new ArrayList<>(conditions.size() + other.conditions.size());
        list.addAll(conditions);
        list.addAll(other.conditions);
        return new TNameMatcher(list);
    }

    public static enum Result {
        /** {@link TName} doesn't match */
        REJECT,
        /** Eats nodes up to a match */
        NEXT,
        /** {@link TName} matches */
        OK
    }

    public static interface Condition {
        Result matches(String value);
    }

    private static class FixedCondition implements Condition {
        private final String fixedValue;

        public FixedCondition(String fixedValue) {
            this.fixedValue = fixedValue;
        }

        @Override
        public Result matches(String value) {
            return fixedValue.equals(value) ? Result.OK : Result.REJECT;
        }

        @Override
        public String toString() {
            return "'" + fixedValue + "'";
        }
    }

    private static class RegexpCondition implements Condition {
        private final Pattern pattern;

        public RegexpCondition(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        @Override
        public Result matches(String value) {
            return pattern.matcher(value).matches() ? Result.OK : Result.REJECT;
        }

        @Override
        public String toString() {
            return "/" + pattern.toString() + "/";
        }
    }

    private static final String[] COMPARATOR_SIGN = {">", "=", "<"};

    private static class ComparatorCondition implements Condition {
        private final double value;
        private final int comparator;

        public ComparatorCondition(double value, int comparator) {
            this.value = value;
            this.comparator = comparator;
        }

        @Override
        public Result matches(String name) {
            try {
                double d = Double.parseDouble(name);
                return (Double.compare(d, value) == comparator) ?
                        Result.OK : Result.REJECT;
            } catch (NumberFormatException e) {
                return Result.REJECT;
            }
        }

        @Override
        public String toString() {
            return COMPARATOR_SIGN[comparator] + " " + value;
        }
    }

    private static class IntervalCondition implements Condition {
        private final double from, to;

        public IntervalCondition(double from, double to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public Result matches(String name) {
            try {
                double d = Double.parseDouble(name);
                return (from <= d && d <= to) ?
                        Result.OK : Result.REJECT;
            } catch (NumberFormatException e) {
                return Result.REJECT;
            }
        }

        @Override
        public String toString() {
            return from + " -> " + to;
        }
    }

    private static final Condition JOLLY = new Condition() {
        @Override
        public Result matches(String value) {
            return Result.OK;
        }

        @Override
        public String toString() {
            return "?";
        }
    };

    private static final Condition ALL = new Condition() {
        @Override
        public Result matches(String value) {
            return Result.NEXT;
        }

        @Override
        public String toString() {
            return "*";
        }
    };

    public boolean matches(CharSequence name) {
        return matches(TName.ROOT.append(name.toString()));
    }

    public boolean matches(TName tname) {
        String[] tnames = tname.toArray();
        int size = tnames.length;
        Result previousMatch = Result.OK;
        Result match = Result.OK;
        int index = 0;
        for (Condition node : conditions) {
            if (index == size) {
                return node == ALL &&
                    Objects.equals(conditions.get(conditions.size() - 1), node);
            }
            if (previousMatch == Result.NEXT) {
                for (int i=index, s=size; i<s; i++) {
                    match = node.matches(tnames[i]);
                    if (match == Result.OK) {
                        index += i;
                        break;
                    }
                }
            } else {
                String value = tnames[index];
                match = node.matches(value);
            }
            switch (match) {
                case REJECT: return false;
                case OK: index++; break;
            }
            previousMatch = match;
        }
        return previousMatch == Result.NEXT || index >= size;
    }

    @Override
    public String toString() {
        return conditions.toString();
    }
}
