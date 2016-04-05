package com.fillumina.performance.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultipleMeasure {
    private final Measure[] measures;
    private final int measuresCount;
    private final long totalSamples;
    private final double meanSquareAmong;
    private final double meanSquareWithin;
    private final double anova;

    public MultipleMeasure(Measure global, Measure... measures) {
        this.measures = measures;
        this.measuresCount = measures.length;
        double sumOfSquareAmong = 0;
        double sumOfSquareWithin = 0;
        long count = 0;
        final double globalMean = global.mean();
        double value;
        Measure stat;
        for (int i=0; i<measuresCount; i++) {
            stat = measures[i];
            value = (stat.mean() - globalMean);
            sumOfSquareAmong += stat.count() * value * value;
            sumOfSquareWithin += stat.variance() * stat.count();
            count += measures[i].count();
        }
        this.totalSamples = count;
        long dfNum = measuresCount - 1;
        long dfDen = count - measuresCount;

        meanSquareAmong = sumOfSquareAmong / dfNum;
        meanSquareWithin = sumOfSquareWithin / dfDen;
        anova = meanSquareAmong / meanSquareWithin;
    }

    /**
     * Calculates the Tukey HSD test using the Tukey - Cramer formula.
     * Assumes that the populations have equal variances but can have
     * different number of samples.
     *
     * @see <a href='https://web.mst.edu/~psyworld/tukeyssteps.htm'>
     *  Tukey's HSD Posto Hoc Test</a>
     */
    public double tukeyKramerHsdQStat(int a, int b) {
        final Measure ma = measures[a];
        double mean1 = ma.mean();
        final long n1 = ma.count();

        final Measure mb = measures[b];
        double mean2 = mb.mean();
        final long n2 = mb.count();

        //double s = Math.sqrt((r1 + r2) / 2.0);
        double s = Math.sqrt(getMeanSquareWithin() / (2.0 / (1.0/n1 + 1.0/n2)));
        return Math.abs(mean1 - mean2) / s;
    }

    /**
     * Probability Tukey's HSD value.
     * Assumes that the populations have equal variances but can have
     * different number of samples.
     *
     * @see <a href='https://www.uvm.edu/~dhowell/gradstat/psych341/labs/Lab1/Multcomp.html'>
     *  Multiple Comparisons With Unequal Sample Sizes</a>
     */
    public double tukeyKramerHsdPValue(int a, int b) {
        double q = tukeyKramerHsdQStat(a, b);
        return Qsturng.pStudentRange(q, measuresCount,
                totalSamples - measuresCount);
    }

    /**
     * Checks if the two measures are statistically different.
     * Assumes that the populations have equal variances but can have
     * different number of samples. This test is more permissive than
     * the Games - Howell's.
     *
     * @param confidence = (1 - alpha) [alpha = significance level]
     *        the confidence level required for the check (i.e. 0.95)
     * @param index1 index of the first measure (same order as inserted)
     * @param index2 index of the second measure (same order as inserted)
     * @return true if the two measures are different
     */
    public boolean areSignificanltyDifferentAccordingToTukeyKramer(
            double confidence, int index1, int index2) {
        return tukeyKramerHsdPValue(index1, index2) > confidence;
    }

    /**
     * Calculates the Tukey HSD test using the Games - Howell formula.
     * Populations might have different variances and number of samples.
     *
     * @see <a href='https://www.uvm.edu/~dhowell/gradstat/psych341/labs/Lab1/Multcomp.html'>
     *  Multiple Comparisons With Unequal Sample Sizes</a>
     */
    public double gamesHowellQStat(int a, int b) {
        final Measure ma = measures[a];
        double mean1 = ma.mean();
        final long n1 = ma.count();
        double r1 = ma.variance() / n1;

        final Measure mb = measures[b];
        double mean2 = mb.mean();
        final long n2 = mb.count();
        double r2 = mb.variance() / n2;

        double s = Math.sqrt((r1 + r2) / 2.0);
        return Math.abs(mean1 - mean2) / s;
    }

    /**
     * Populations might have different variances and number of samples.
     *
     * @param a
     * @param b
     * @return
     *
     * @see <a href='https://www.uvm.edu/~dhowell/gradstat/psych341/labs/Lab1/Multcomp.html'>
     *  Multiple Comparisons With Unequal Sample Sizes</a>
     */
    public double gamesHowellPValue(int a, int b) {
        double var1 = measures[a].variance();
        long n1 = measures[a].count();
        double r1 = var1 / n1;
        double var2 = measures[b].variance();
        long n2 = measures[b].count();
        double r2 = var2 / n2;
        double df = pow2(r1 + r2) / (pow2(r1)/(n1-1) + pow2(r2)/(n2-1));
        double q = gamesHowellQStat(a, b);
        return Qsturng.pStudentRange(q, measuresCount, df);
    }

    /**
     * Checks if the two measures are statistically different according
     * to the Games - Howell formula (1976).
     * Populations might have different variances and number of samples.
     *
     * @param confidence = (1 - alpha) [alpha = significance level]
     *        the confidence level required for the check (i.e. 0.95)
     * @param index1 index of the first measure (same order as inserted)
     * @param index2 index of the second measure (same order as inserted)
     * @return true if the two measures are different
     *
     * @see <a href='https://www.uvm.edu/~dhowell/gradstat/psych341/labs/Lab1/Multcomp.html'>
     *  Multiple Comparisons With Unequal Sample Sizes</a>
     */
    public boolean areSignificanltyDifferentAccordingToGamesHowell(
            double confidence, int index1, int index2) {
        return gamesHowellPValue(index1, index2) > confidence;
    }

    private static double pow2(double x) {
        return x * x;
    }

    /**
     * @param confidence {@code confidence level = 1 - alpha}
     *                   (alpha is the significance level)
     */
    public boolean isStatisticallyRelevantWithConfidence(double confidence) {
        long dfNum = measuresCount - 1;
        long dfDen = totalSamples - measuresCount;
        double f = StatFunctions.inverseFishF(1 - confidence, dfNum, dfDen);
        return anova > f;
    }

    /**
     * If the p-value corresponding to the F-statistic of one-way ANOVA.
     * It's the probability the measures are significant.
     */
    public double anovaPValue() {
        long dfNum = measuresCount - 1;
        long dfDen = totalSamples - measuresCount;
        return StatFunctions.fishF(anova, dfNum, dfDen);
    }

    /** Also said MS<sub>Treatments</sub> */
    public double getMeanSquareAmong() {
        return meanSquareAmong;
    }

    /** Also said MS<sub>error</sub> */
    public double getMeanSquareWithin() {
        return meanSquareWithin;
    }

    public double getAnova() {
        return anova;
    }

    public int getMeasureCount() {
        return measuresCount;
    }

    public long getTotalSamples() {
        return totalSamples;
    }
}
