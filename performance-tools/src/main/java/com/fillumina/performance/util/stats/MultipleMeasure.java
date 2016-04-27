package com.fillumina.performance.util.stats;

import java.util.AbstractList;
import java.util.List;

/**
 * Calculates statistical significance between different measures or
 * experiments using ANOVA for multiple significance and Tukey-Kramer and
 * Games-Howell for significance between pairs.
 *
 * @see RunningMultipleMeasure
 * @see <a href='http://sphweb.bumc.bu.edu/otlt/MPH-Modules/BS/BS704_HypothesisTesting-ANOVA/BS704_HypothesisTesting-Anova_print.html'>
 *  ANOVA</a>
 * @see <a href='https://en.wikipedia.org/wiki/Analysis_of_variance'>
 *  Wikipedia: ANOVA</a>
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultipleMeasure {
    public static final MultipleMeasure EMPTY =
            new MultipleMeasure(OnlineMeasure.EMPTY, OnlineMeasure.EMPTY);

    private final OnlineMeasure global;
    private final OnlineMeasure[] measures;
    private final int measuresCount;
    private final long totalSamples;
    private final double meanSquareBetween;
    private final double meanSquareWithin;
    private final double anovaF;
    private final List<OnlineMeasure> unmodifiableList = new AbstractList<OnlineMeasure>() {
        @Override
        public OnlineMeasure get(int index) {
            return measures[index];
        }

        @Override
        public int size() {
            return measures.length;
        }
    };

    /**
     *
     * @param global    all the samples from all the measures
     * @param measures  the different measures to be compared
     */
    public MultipleMeasure(OnlineMeasure global, OnlineMeasure... measures) {
        this.global = global;
        this.measures = measures;
        this.measuresCount = measures.length;
        double sumOfSquareAmong = 0;
        double sumOfSquareWithin = 0;
        long count = 0;
        final double globalMean = global.mean();
        double value;
        OnlineMeasure stat;
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

        meanSquareBetween = sumOfSquareAmong / dfNum;
        meanSquareWithin = sumOfSquareWithin / dfDen;
        anovaF = meanSquareBetween / meanSquareWithin;
    }

    /**
     * Calculates the Tukey HSD test using the Tukey - Cramer formula.
     * Assumes that the populations have equal variances but can have
     * different number of samples.
     *
     * @see <a href='https://web.mst.edu/~psyworld/tukeyssteps.htm'>
     *  Tukey's HSD Posto Hoc Test</a>
     */
    public double tukeyKramerHsdQStat(int idx1, int idx2) {
        final OnlineMeasure ma = measures[idx1];
        double mean1 = ma.mean();
        final long n1 = ma.count();

        final OnlineMeasure mb = measures[idx2];
        double mean2 = mb.mean();
        final long n2 = mb.count();

        //double s = Math.sqrt((r1 + r2) / 2.0);
        double s = Math.sqrt(getAnovaMeanSquareWithin() / (2.0 / (1.0/n1 + 1.0/n2)));
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
    public double tukeyKramerHsdPValue(int idx1, int idx2) {
        double q = tukeyKramerHsdQStat(idx1, idx2);
        return Qsturng.pStudentRange(q, measuresCount,
                totalSamples - measuresCount);
    }

    /**
     * Checks if the two measures are statistically different.
     * Assumes that the populations have equal variances but can have
     * different number of samples. This test is more permissive than
     * the Games - Howell'.
     * <p>
     * The Tukey Honest Significance Difference (HSD) test find means that
     * are significantly different from each other.
     *
     * @param confidence = (1 - alpha) [alpha = significance level]
     *        the confidence level required for the check (i.e. 0.95)
     * @param index1 index of the first measure (same order as inserted)
     * @param index2 index of the second measure (same order as inserted)
     * @return true if the two measures are different
     *
     * <a href='https://en.wikipedia.org/wiki/Tukey%27s_range_test'>
     *  Wikipedia: Tukey's range test</a>
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
    public double gamesHowellQStat(int idx1, int idx2) {
        final OnlineMeasure ma = measures[idx1];
        double mean1 = ma.mean();
        final long n1 = ma.count();
        double r1 = ma.variance() / n1;

        final OnlineMeasure mb = measures[idx2];
        double mean2 = mb.mean();
        final long n2 = mb.count();
        double r2 = mb.variance() / n2;

        double s = Math.sqrt((r1 + r2) / 2.0);
        return Math.abs(mean1 - mean2) / s;
    }

    /**
     * Gives the probability of two measures to be different according to the
     * Games - Howell formula.
     * Populations might have different variances and number of samples.
     *
     * @param idx1
     * @param idx2
     * @return
     *
     * @see <a href='https://www.uvm.edu/~dhowell/gradstat/psych341/labs/Lab1/Multcomp.html'>
     *  Multiple Comparisons With Unequal Sample Sizes</a>
     */
    public double gamesHowellPValue(int idx1, int idx2) {
        double var1 = measures[idx1].variance();
        long n1 = measures[idx1].count();
        double r1 = var1 / n1;
        double var2 = measures[idx2].variance();
        long n2 = measures[idx2].count();
        double r2 = var2 / n2;
        double df = pow2(r1 + r2) / (pow2(r1)/(n1-1) + pow2(r2)/(n2-1));
        double q = gamesHowellQStat(idx1, idx2);
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
        return anovaF > f;
    }

    /**
     * It's the probability the measures are significant according to ANOVA.
     */
    public double anovaPValue() {
        if (Double.isFinite(anovaF)) {
            long dfNum = measuresCount - 1;
            long dfDen = totalSamples - measuresCount;
            return StatFunctions.fishF(anovaF, dfNum, dfDen);
        }
        return 0;
    }

    /**
     * MS<sub>Among</sub> also said MS<sub>Treatments</sub> and
     * MS<sub>Between</sub>.
     */
    public double getAnovaMeanSquareBetween() {
        return meanSquareBetween;
    }

    /** MS<sub>Within</sub> also said MS<sub>error</sub> */
    public double getAnovaMeanSquareWithin() {
        return meanSquareWithin;
    }

    /** @return the ANVOA F critical value of the measures. */
    public double getAnovaF() {
        return anovaF;
    }

    /** @return how many measures are considered. */
    public int getMeasureCount() {
        return measuresCount;
    }

    /** Returns the measure of all the tests together. */
    public OnlineMeasure getGlobal() {
        return global;
    }

    /** Returns the single measures. */
    public List<OnlineMeasure> getMeasures() {
        return unmodifiableList;
    }

    /**
     * @return total number of samples (sum of samples number on each measure).
     */
    public long getTotalSamples() {
        return totalSamples;
    }
}
