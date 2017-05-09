package com.fillumina.performance.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMatcher {

    private final List<Condition> conditions;

    public static class Builder {
        private final List<Condition> conditions = new ArrayList<>();

        /** Matches a fixed string. */
        public Builder string(String name) {
            conditions.add(new FixedCondition(name));
            return this;
        }

        /** Matches numbers less than the given one. */
        public Builder lessThan(double value) {
            conditions.add(new ComparatorCondition(value, -1));
            return this;
        }

        /** Matches numbers greater than the given one. */
        public Builder greaterThan(double value) {
            conditions.add(new ComparatorCondition(value, 1));
            return this;
        }

        /** Matches numbers equals to the given one. */
        public Builder equalsTo(double value) {
            conditions.add(new ComparatorCondition(value, 0));
            return this;
        }

        /** Matches numbers between the given interval (inclusive). */
        public Builder interval(double from, double to) {
            conditions.add(new IntervalCondition(from, to));
            return this;
        }

        /** Matches the given REGEXP pattern. */
        public Builder pattern(String pattern) {
            conditions.add(new RegexpCondition(pattern));
            return this;
        }

        /** Matches a single name (same as .? in REGEXP). */
        public Builder jolly() {
            conditions.add(JOLLY);
            return this;
        }

        /** Matches zero or more names (same as .* in REGEXP). */
        public Builder all() {
            conditions.add(ALL);
            return this;
        }

        /** Adds a user defined conditions. */
        public Builder condition(Condition matcherNode) {
            conditions.add(matcherNode);
            return this;
        }

        public TNameMatcher build() {
            return new TNameMatcher(conditions);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private TNameMatcher(List<Condition> conditions) {
        this.conditions = conditions;
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
    }

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
    }

    private static final Condition JOLLY = new Condition() {
        @Override
        public Result matches(String value) {
            return Result.OK;
        }
    };

    private static final Condition ALL = new Condition() {
        @Override
        public Result matches(String value) {
            return Result.NEXT;
        }
    };

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
}
