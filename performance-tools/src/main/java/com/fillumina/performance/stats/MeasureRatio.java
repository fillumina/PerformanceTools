package com.fillumina.performance.stats;

/**
 * Computes the confidence interval of the ratio of two normal means.
 *
 * @see <a href='http://stats.stackexchange.com/questions/16349/how-to-compute-the-confidence-interval-of-the-ratio-of-two-normal-means'>
 *  StackExchange: How to compute the confidence interval of the ratio of two normal means</a>
 * @see <a href='http://www.graphpad.com/FAQ/images/Ci%20of%20quotient.pdf'>
 *  Harvey J. Motulsky: Confidence Interval of a ratio of two means (PDF)</a>
 * @see <a href='https://en.wikipedia.org/wiki/Fieller%27s_theorem'>
 *  Wikipedia: Fieller's Theorem</a>
 * 
 * @author Harvey J. Motulsky
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureRatio {

    private final boolean valid;
    private final double ratio;
    private final long count;
    private final double standardError;
    private final double marginOfError;

    public MeasureRatio(Measure statA, Measure statB,
            double confidence) {
        this(statA.mean(), statA.variance(), statA.count(),
                statB.mean(), statB.variance(), statB.count(),
                confidence);
    }

    public MeasureRatio(
            double meanA, double varA, long countA,
            double meanB, double varB, long countB,
            double confidence) {
        count = countA + countB;
        double g = StatFunctions.student(confidence, count - 2) *
                sem(varB, countB) / meanB;
        g *= g;
        valid = g < 1;
        if (!valid) {
            ratio = meanA / meanB;
            standardError = -1;
            marginOfError = -1;
            return;
        }
        ratio = meanA / (meanB * (1 - g));
        double semA = sem(varA, countA);
        double semB = sem(varB, countB);
        standardError = ratio * Math.sqrt(
                ((1 - g) * (semA * semA) / (meanA * meanA) +
                (semB * semB) / (meanB * meanB)));
        marginOfError = standardError *
                StatFunctions.student(confidence, count - 2);
    }

    /** Is the result valid. */
    public boolean isValid() {
        return valid;
    }

    public double getRatio() {
        return ratio;
    }

    public long getCount() {
        return count;
    }

    public double getStandardError() {
        return standardError;
    }

    public double getMarginOfError() {
        return marginOfError;
    }

    /**
     * Standard error of the mean.
     * @see <a href='http://www.sportsci.org/resource/stats/meansd.html'>
     *  Standard Error of the mean</a>
     */
    private double sem(double variance, long samples) {
        return Math.sqrt(variance / samples);
    }

    @Override
    public String toString() {
        if (!valid) {
            return "" + ratio + " (not statistically valid)";
        }
        return "" + ratio + " ± " + marginOfError;
    }
}

