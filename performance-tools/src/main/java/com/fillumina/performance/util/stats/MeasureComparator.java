package com.fillumina.performance.util.stats;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureComparator implements Comparator<Measure>, Serializable {
    private static final long serialVersionUID = 1L;
    private final double confidence;

    public MeasureComparator(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public int compare(Measure o1, Measure o2) {
        ConfidenceInterval a = o1.getConfidenceInterval(confidence);
        ConfidenceInterval b = o2.getConfidenceInterval(confidence);
        if (a.getUpperBound() < b.getLowerBound()) {
            return -1;
        } else if (b.getUpperBound() < a.getLowerBound()) {
            return 1;
        }
        return 0;
    }

}
