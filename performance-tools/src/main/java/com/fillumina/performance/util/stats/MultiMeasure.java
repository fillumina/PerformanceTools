package com.fillumina.performance.util.stats;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
public class MultiMeasure {
    private final Measure global;
    private final Measure[] measures;
    private final int measuresCount;
    private final long totalNumberOfSamples;
    private final double meanSquareBetween;
    private final double meanSquareWithin;
    private final double anovaF;
    private final List<Measure> unmodifiableList = new AbstractList<Measure>() {
        @Override
        public Measure get(int index) {
            return measures[index];
        }

        @Override
        public int size() {
            return measures.length;
        }
    };

    public static MultiMeasure add(MultiMeasure a, Measure measure) {
        double total = 0.0;
        int count = 0;
        Measure[] all = new Measure[a.measures.length + 1];
        int index = 0;
        for (Measure m : a.measures) {
            total += m.getSum();
            count += m.getCount();
            all[index] = m;
            index++;
        }
        total += measure.getSum();
        count += measure.getCount();
        all[index] = measure;
        index++;
        double globalMean = total / count;
        Measure global = new OnlineMeasure(globalMean);
        return new MultiMeasure(global, all);
    }

    public static MultiMeasure join(MultiMeasure a, MultiMeasure b) {
        double total = 0.0;
        int count = 0;
        Measure[] all = new Measure[a.measures.length + b.measures.length];
        int index = 0;
        for (Measure m : a.measures) {
            total += m.getSum();
            count += m.getCount();
            all[index] = m;
            index++;
        }
        for (Measure m : b.measures) {
            total += m.getSum();
            count += m.getCount();
            all[index] = m;
            index++;
        }
        double globalMean = total / count;
        Measure global = new OnlineMeasure(globalMean);
        return new MultiMeasure(global, all);
    }

    public static MultiMeasure createFrom(Measure... measures) {
        MultiMeasure global =
                new MultiMeasure(measures[0], new Measure[]{measures[0]});
        for (int i=1; i<measures.length; i++) {
            global = add(global, measures[i]);
        }
        return global;
    }

    /**
     *
     * @param global    all the samples from all the measures
     * @param measures  the different measures to be compared
     */
    public MultiMeasure(Measure global, Measure... measures) {
        this.global = global;
        this.measures = measures;
        this.measuresCount = measures.length;
        double sumOfSquareAmong = 0;
        double sumOfSquareWithin = 0;
        long samples = 0;
        final double globalMean = global.getMean();
        double value;
        Measure stat;
        for (int i=0; i<measuresCount; i++) {
            stat = measures[i];
            value = (stat.getMean() - globalMean);
            sumOfSquareAmong += stat.getCount() * value * value;
            sumOfSquareWithin += stat.getVariance() * stat.getCount();
            samples += measures[i].getCount();
        }
        this.totalNumberOfSamples = samples;
        long dfNum = measuresCount - 1;
        long dfDen = samples - measuresCount;

        meanSquareBetween = sumOfSquareAmong / dfNum;
        meanSquareWithin = sumOfSquareWithin / dfDen;
        anovaF = meanSquareBetween / meanSquareWithin;
    }

    /**
     * Calculates the Tukey HSD test using the Tukey - Kramer formula.
     * Assumes that the populations have equal variances but can have
     * different number of samples.
     *
     * @see <a href='https://web.mst.edu/~psyworld/tukeyssteps.htm'>
     *  Tukey's HSD Post Hoc Test</a>
     */
    public double tukeyKramerHsdQStat(int idx1, int idx2) {
        final Measure ma = measures[idx1];
        double mean1 = ma.getMean();
        final long n1 = ma.getCount();

        final Measure mb = measures[idx2];
        double mean2 = mb.getMean();
        final long n2 = mb.getCount();

        //double s = Math.sqrt((r1 + r2) / 2.0);
        double s = Math.sqrt(getAnovaMeanSquareWithin() /
                (2.0 / (1.0/n1 + 1.0/n2)));
        return Math.abs(mean1 - mean2) / s;
    }

    /**
     * Probability Tukey's HSD value.
     * Assumes that the populations have equal variances but can have
     * different number of samples.
     *
     * @see <a href='https://www.uvm.edu/~dhowell/gradstat/psych341/labs/Lab1/Multcomp.html'>
     *  Multiple Comparisons With Unequal Sample Sizes</a>
     *
     * @return the probability the two measures are relatable
     */
    public double tukeyKramerHsdPValue(int idx1, int idx2) {
        double q = tukeyKramerHsdQStat(idx1, idx2);
        if (totalNumberOfSamples - measuresCount <= 2) {
            throw new IllegalArgumentException(
                    "totalNumberOfSamples - measuresCount must be > 2 : " +
                    "totalNumberOfSamples = " + totalNumberOfSamples +
                    ", measureCount = " + measuresCount);
        }
        return Qsturng.pStudentRange(q, measuresCount,
                totalNumberOfSamples - measuresCount);
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
        final Measure ma = measures[idx1];
        double mean1 = ma.getMean();
        final long n1 = ma.getCount();
        double r1 = ma.getVariance() / n1;

        final Measure mb = measures[idx2];
        double mean2 = mb.getMean();
        final long n2 = mb.getCount();
        double r2 = mb.getVariance() / n2;

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
        double var1 = measures[idx1].getVariance();
        long n1 = measures[idx1].getCount();
        double r1 = var1 / n1;
        double var2 = measures[idx2].getVariance();
        long n2 = measures[idx2].getCount();
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
        long dfDen = totalNumberOfSamples - measuresCount;
        double f = StatFunctions.inverseFishF(1 - confidence, dfNum, dfDen);
        return anovaF > f;
    }

    /**
     * It's the probability the measures are statistically significant
     * (different) according to ANOVA.
     *
     * @return the probability some of the tests are significantly relatable
     */
    public double anovaPValue() {
        if (!Double.isInfinite(anovaF) && !Double.isNaN(anovaF)) {
            long dfNum = measuresCount - 1;
            long dfDen = totalNumberOfSamples - measuresCount;
            return 1 - StatFunctions.fishF(anovaF, dfNum, dfDen);
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
    public Measure getGlobal() {
        return global;
    }

    /** Returns the single measures. */
    public List<Measure> getMeasures() {
        return unmodifiableList;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.global.getMean());
        hash = 37 * hash + Arrays.deepHashCode(this.measures);
        hash = 37 * hash + this.measuresCount;
        hash =
                37 * hash +
                (int) (this.totalNumberOfSamples ^
                (this.totalNumberOfSamples >>> 32));
        hash =
                37 * hash +
                (int) (Double.doubleToLongBits(this.meanSquareBetween) ^
                (Double.doubleToLongBits(this.meanSquareBetween) >>> 32));
        hash =
                37 * hash +
                (int) (Double.doubleToLongBits(this.meanSquareWithin) ^
                (Double.doubleToLongBits(this.meanSquareWithin) >>> 32));
        hash =
                37 * hash +
                (int) (Double.doubleToLongBits(this.anovaF) ^
                (Double.doubleToLongBits(this.anovaF) >>> 32));
        hash = 37 * hash + Objects.hashCode(this.unmodifiableList);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MultiMeasure other = (MultiMeasure) obj;
        if (this.measuresCount != other.measuresCount) {
            return false;
        }
        if (this.totalNumberOfSamples != other.totalNumberOfSamples) {
            return false;
        }
        if (Double.doubleToLongBits(this.meanSquareBetween) !=
                Double.doubleToLongBits(other.meanSquareBetween)) {
            return false;
        }
        if (Double.doubleToLongBits(this.meanSquareWithin) !=
                Double.doubleToLongBits(other.meanSquareWithin)) {
            return false;
        }
        if (Double.doubleToLongBits(this.anovaF) !=
                Double.doubleToLongBits(other.anovaF)) {
            return false;
        }
        if (!Objects.equals(this.global.getMean(), other.global.getMean())) {
            return false;
        }
        if (!Arrays.deepEquals(this.measures, other.measures)) {
            return false;
        }
        return Objects.equals(this.unmodifiableList, other.unmodifiableList);
    }

    @Override
    public String toString() {
        return "MultiMeasure{" + "global=" + global +
                ", measures=" + measures +
                ", measuresCount=" + measuresCount +
                ", totalNumberOfSamples=" + totalNumberOfSamples + '}';
    }
}
