package com.fillumina.performance.util.stats;

/**
 * Represents a measure with its confidence interval.
 * Note that the confidence interval might be asymmetrical that's why
 * the margin of error is not reported here.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ConfidenceInterval extends Comparable<ConfidenceInterval> {

    /** The mean of the measure. */
    double getValue();

    /** The lower bound of the interval. */
    double getLowerBound();

    /** The upper bound of the interval. */
    double getUpperBound();

    /**
     * The confidence level expressed as a fraction (i.e. 0.95 for 95%).
     * It has the following relation with the significance level (alpha):
     * {@code confidence_level = 1 - alpha}.
     * level.
     */
    double getConfidence();
}
