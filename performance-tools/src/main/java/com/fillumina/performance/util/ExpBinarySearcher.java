package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExpBinarySearcher {

    /** @see #search(int, int, java.lang.Comparable) */
    public static int search(int end, Comparable<Integer> comparable) {
        return ExpBinarySearcher.search(0, end, comparable);
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
     *        <li>0 in case of equality (optional)
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
