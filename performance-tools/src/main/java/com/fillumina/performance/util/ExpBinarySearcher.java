package com.fillumina.performance.util;

/**
 * Having a condition that holds from (or up to) a certain value of an
 * increasing sequence this algorithm searches efficiently for the
 * first (or the last) value of the sequence where it holds (including or
 * excluding).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExpBinarySearcher {

    public interface Condition {
        boolean isSatisfied(int value);
    }

    private static class NegateCondition implements Condition {
        private final Condition condition;

        public NegateCondition(Condition condition) {
            this.condition = condition;
        }

        @Override
        public boolean isSatisfied(int value) {
            return !condition.isSatisfied(value);
        }

    }

    /**
     * Searches for the first value for which the condition holds.
     *
     * @param start is the first value of the sequence (must be positive or 0)
     * @param end   maximum value of the sequence
     * @param condition the condition to check over.
     * @return the first value for which the condition holds (or -1 if no value).
     */
    public static int searchGreaterOrEquals(int start, int end,
            final Condition condition) {
        return excludingSearch(start, end, new NegateCondition(condition));
    }

    /**
     * Searches for the last value for which the condition doesn't hold.
     *
     * @param start is the first value of the sequence (must be positive or 0)
     * @param end   maximum value of the sequence
     * @param condition the condition to check over.
     * @return the last value for which the condition doesn't hold
     *          (or -1 if no value).
     */
    public static int searchGreater(int start, int end,
            final Condition condition) {
        return includingSearch(start, end, new NegateCondition(condition));
    }

    /**
     * Searches for the last value for which the condition holds.
     *
     * @param start is the first value of the sequence (must be positive or 0)
     * @param end   maximum value of the sequence
     * @param condition the condition to check over.
     * @return the last value for which the condition holds (or -1 if no value).
     */
    public static int searchLessOrEquals(int start, int end,
            final Condition condition) {
        return includingSearch(start, end, condition);
    }

    /**
     * Searches for the first value for which the condition doesn't hold.
     *
     * @param start is the first value of the sequence (must be positive or 0)
     * @param end   maximum value of the sequence
     * @param condition the condition to check over.
     * @return the first value for which the condition doesn't hold
     *         (or -1 if no value).
     */
    public static int searchLess(int start, int end,
            final Condition condition) {
        return excludingSearch(start, end, condition);
    }

    static int includingSearch(int start, int end,
            final Condition condition) {
        return search(start, end, new Comparable<Integer>() {
            @Override
            public int compareTo(Integer o) {
                if (condition.isSatisfied(o)) {
                    return -1;
                }
                return 1;
            }
        });
    }

    static int excludingSearch(int start, int end,
            final Condition condition) {
        return search(start + 1, end, new Comparable<Integer>() {
            @Override
            public int compareTo(Integer o) {
                if (condition.isSatisfied(o - 1)) {
                    return -1;
                }
                return 1;
            }
        });
    }

    /**
     * Omit the start value which is set to 0.
     *
     * @see #search(int, int, java.lang.Comparable)
     */
    public static int search(int end, Comparable<Integer> comparable) {
        return search(0, end, comparable);
    }

    /**
     * Use this form when the comparator reports the condition as {@code >}
     * instead of {@code >=}.
     *
     * @see #search(int, int, java.lang.Comparable)
     */
    public static int altSearch(int start, int end, Comparable<Integer> comparable) {
        return 1 + search(start - 1, end, comparable);
    }

    /**
     * Finds the last value of an increasing sequence of numbers for which a
     * certain condition holds. It first increase the value exponentially and
     * if the condition doesn't hold anymore it bisects the interval to
     * search for the last value where it still holds.
     *
     * @param start is the first value of the sequence (must be positive or 0)
     * @param end   maximum value of the sequence
     * @param comparable the condition to check over.
     *        Note that the value is found even in the
     *        absence of the equality condition (0).
     *        Of course there will be more checks.
     *        Comparable should return:
     *        <ul>
     *        <li>0 in case of equality (optional, might be always false)
     *        <li>1 the condition doesn't hold (false)
     *        <li>-1 the condition holds (true)
     *        </ul>
     * @return the last value in the sequence for which the given condition
     *         holds or {@code -1} if no elements satisfies it.
     */
    public static int search(int start, int end, Comparable<Integer> comparable) {
        int max = end - start;
        int logIncrement = -2;
        boolean exp = true;
        boolean up = true;
        int current = 0;
        int lastOk = current;

        while (current >= 0 && (current < max || !up)) {
            if (exp) {
                logIncrement++;
                if (logIncrement == -1) {
                    current = 0;
                } else {
                    current = 1 << logIncrement;
                }
            } else {
                if (logIncrement <= 0) {
                    return lastOk;
                }
                logIncrement--;
                if (up) {
                    current += 1 << logIncrement;
                } else {
                    current -= 1 << logIncrement;
                }
            }

            if (current < 0) {
                // overflow
                return -1;
            }

            final int value = current + start;

            //System.out.println("current = " + value);
            switch(comparable.compareTo(value)) {
                case 0:
                    return value;
                case 1:
                    if (exp) {
                        exp = false;
                        logIncrement--;
                    }
                    up = false;
                    break;
                case -1:
                    lastOk = value;
                    up = true;
            }
        }
        return -1;
    }
}
