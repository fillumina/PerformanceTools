package com.fillumina.performance.util.stats;

import java.util.Comparator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractConfidenceInterval
        implements ConfidenceInterval, Comparator<ConfidenceInterval> {

    @Override
    public int compareTo(ConfidenceInterval o) {
        return compare(this, o);
    }

    @Override
    public int compare(ConfidenceInterval a, ConfidenceInterval b) {
        if (a.getUpperBound() < b.getLowerBound()) {
            return -1;
        } else if (b.getUpperBound() < a.getLowerBound()) {
            return 1;
        }
        return 0;
    }
}
