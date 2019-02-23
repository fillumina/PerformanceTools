package com.fillumina.performance.util.pathname;

import com.fillumina.performance.util.CallBackBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A matcher that matches {@link PathName}s.
 * It allows to create expressions that match {@link PathName}s.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PathNameMatcher {
    public static final PathNameMatcher EMPTY =
            new PathNameMatcher(Collections.<Condition>emptyList());

    private final List<Condition> conditions;

    /** *  Build a matcher that matches {@link PathName}s. */
    public static class MatcherBuilder<C> extends CallBackBuilder<C,PathNameMatcher> {
        private final List<Condition> conditions = new ArrayList<>();

        private MatcherBuilder() {
            super();
        }

        private MatcherBuilder(Setter<C, PathNameMatcher> setter) {
            super(setter);
        }

        /** Matches a fixed string. */
        public MatcherBuilder<C> string(String... names) {
            for (String n : names) {
                conditions.add(new FixedCondition(n));
            }
            return this;
        }

        /** Matches numbers less than the given one. */
        public MatcherBuilder<C> lessThan(double value) {
            conditions.add(new ComparatorCondition(value, -1));
            return this;
        }

        /** Matches numbers greater than the given one. */
        public MatcherBuilder<C> greaterThan(double value) {
            conditions.add(new ComparatorCondition(value, 1));
            return this;
        }

        /** Matches numbers equals to the given one. */
        public MatcherBuilder<C> equalsTo(double value) {
            conditions.add(new ComparatorCondition(value, 0));
            return this;
        }

        /** Matches numbers between the given interval (inclusive). */
        public MatcherBuilder<C> interval(double from, double to) {
            conditions.add(new IntervalCondition(from, to));
            return this;
        }

        /** Matches the given REGEXP pattern. */
        public MatcherBuilder<C> pattern(String pattern) {
            conditions.add(new RegexpCondition(pattern));
            return this;
        }

        /** Matches a single name (same as .? in REGEXP). */
        public MatcherBuilder<C> jolly() {
            conditions.add(JOLLY);
            return this;
        }

        /** Matches zero or more names (same as .* in REGEXP). */
        public MatcherBuilder<C> all() {
            conditions.add(ALL);
            return this;
        }

        /** Adds a user defined conditions. */
        public MatcherBuilder<C> condition(Condition matcherNode) {
            conditions.add(matcherNode);
            return this;
        }

        @Override
        public PathNameMatcher build() {
            return new PathNameMatcher(conditions);
        }
    }

    public static MatcherBuilder<PathNameMatcher> builder() {
        return new MatcherBuilder<>();
    }

    public static <C> MatcherBuilder<C> builder(
            CallBackBuilder.Setter<C,PathNameMatcher> setter) {
        return new MatcherBuilder<>(setter);
    }

    private PathNameMatcher(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public PathNameMatcher append(PathNameMatcher other) {
        List<Condition> list =
                new ArrayList<>(conditions.size() + other.conditions.size());
        list.addAll(conditions);
        list.addAll(other.conditions);
        return new PathNameMatcher(list);
    }

    public static enum Result {
        /** {@link PathName} doesn't match */
        REJECT,
        /** Eats nodes up to a match */
        NEXT,
        /** {@link PathName} matches */
        ACCEPT
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
            return fixedValue.equals(value) ? Result.ACCEPT : Result.REJECT;
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
            return pattern.matcher(value).matches() ? Result.ACCEPT : Result.REJECT;
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
                        Result.ACCEPT : Result.REJECT;
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
                        Result.ACCEPT : Result.REJECT;
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
            return Result.ACCEPT;
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
        return matches(PathName.ROOT.append(name.toString()));
    }

    public boolean matches(PathName pname) {
        String[] tnames = pname.toArray();
        int size = tnames.length;
        Result previousMatch = Result.ACCEPT;
        Result match = Result.ACCEPT;
        int index = 0;
        for (Condition node : conditions) {
            if (index == size) {
                return node == ALL &&
                    Objects.equals(conditions.get(conditions.size() - 1), node);
            }
            if (previousMatch == Result.NEXT) {
                for (int i=index, s=size; i<s; i++) {
                    match = node.matches(tnames[i]);
                    if (match == Result.ACCEPT) {
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
                case ACCEPT: index++; break;
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
