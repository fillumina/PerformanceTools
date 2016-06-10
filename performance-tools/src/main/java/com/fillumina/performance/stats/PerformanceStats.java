package com.fillumina.performance.stats;

import com.fillumina.performance.stats.formatter.StringTableStatsFormatter;
import com.fillumina.performance.util.Assertion;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MultipleMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Statistics about the experiment.
 * In addition of the usual statistics it calculates ANOVA and performs the
 * Tukey HSD post-hoc test on all experiment pairs so to assess the data
 * collected as statistically significant.
 * <p>
 * This class is immutable.
 *
 * @author Francesco Illuminati
 */
public class PerformanceStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, TestPerformance> testPerformance;
    private final MultipleMeasure multiMeasure;
    private final double[][] tukeyKramerConfidenceMatrix;
    private final double minTukeyKramerConfidence;
    private final double maxPercentageMargin;
    private final long totalTime;

    /**
     *
     * @param description           error message
     * @param global            all samples statistics together (used for ANOVA)
     * @param multimeasure      multiple measure statistics (ANOVA)
     * @param testPerformance   statistics for each test
     */
    public PerformanceStats(OnlineMeasure global,
            MultipleMeasure multimeasure,
            Map<String, TestPerformance> testPerformance) {
        Assertion.isNotNull(global, "global");
        Assertion.isNotNull(multimeasure, "multimeasure");
        Assertion.isNotNull(testPerformance, "testPerformance");

        this.multiMeasure = multimeasure;
        this.testPerformance = testPerformance;

        this.totalTime = calculateGlobalTime(testPerformance);
        this.tukeyKramerConfidenceMatrix =
                calculateTukeyKramerConfidenceMatrix(multiMeasure);
        this.minTukeyKramerConfidence =
                calculateMinTukeyHsdSignificanceProbability(
                        tukeyKramerConfidenceMatrix);

        this.maxPercentageMargin =
                calculateMaxPercentageMargin(testPerformance.values());
    }

    /** @return detailed statistics for each tests in the experiment. */
    public Map<String, TestPerformance> getTestPerformances() {
        return testPerformance;
    }

    /** @return the mean of the elapsed ns per cycle. */
    public Measure getPerformance(String testName)
            throws IllegalStateException {
        try {
            return testPerformance.get(testName).getElapsedNanosecondsPerCycle();
        } catch (NullPointerException e) {
            throw new IllegalStateException(
                    "Test '" + testName +
                    "' not found, valid tests are: " +
                    testPerformance.keySet().toString(), e);
        }
    }

    /**
     * @return the total time spent performing the experiment (in nanoseconds).
     */
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * @return the higher margin of confidence of the ratios of each measure
     *         in the experiment confronted with the slower one. It's an
     *         estimation of the accuracy of the experiment.
     */
    public double getMaximumPercentageMargin() {
        return maxPercentageMargin;
    }

    /**
     * It's an estimation of the statistical significance of the collected data.
     * Returns either the ANOVA probability if it is not bigger than 0.9
     * (that means that the tests cannot be statistically compared or
     * the minimum value of the Tukey HSD test calculated between all the
     * test pairs. Note that the Tukey HSD post hoc test cannot be performed
     * if ANOVA is not statistically significant.
     *
     * @param confidence the required confidence (between 0 and 1, usually 0.9)
     * @return the probability the test is statistically significant.
     */
    public double getStatisticalSignificanceMatrixProbability(double confidence) {
        final double anova = MultipleMeasure.significanceProbability(getAnova());
        if (anova > confidence) {
            return getMinTukeyHsdEvaluationPercentage();
        }
        return anova;
    }

    /**
     * ANOVA (Analysis of Variance) provides a statistical test of whether
     * or not the means of several groups are equal. This probability is close
     * to 1 if at least one pair of means are equal and is close to 0 if all
     * the means are different between each other. If the probability is around
     * 0.5 it means that there aren't enough data (sample count or variance)
     * to provide an answer. This characteristic can be used by algorithms to
     * evaluate if the experiment needs to be repeated with an increased
     * precision (i.e. more iterations).
     *
     * @see https://en.wikipedia.org/wiki/Analysis_of_variance
     * @return the ANOVA percentage value
     */
    public double getAnova() {
        return multiMeasure.anovaPValue();
    }

    /**
     * It's an estimation of the statistical significance of the collected data.
     * The Tukey HSD can be performed only if ANOVA is either close to 0
     * or to 1.
     *
     * @return the minimum value of the Tukey HSD test appied to all test
     *         pairs.
     */
    public double getMinTukeyHsdEvaluationPercentage() {
        return minTukeyKramerConfidence;
    }

    /**
     * The Tukey-Kramer honest significant difference test finds means that are
     * significantly different from each other. Consequently it can find means
     * that are significantly equal or not comparable (without enough data
     * to be statistically significant).
     *
     * @see https://en.wikipedia.org/wiki/Tukey%27s_range_test
     * @return the Tukey-Kramer honest significant difference (HSD) test value
     *         between the two tests with given indexes.
     */
    public double getTukeyKramerHsdConfidenceProbability(
            String test1, String test2) {
        return getTukeyKramerHsdConfidenceProbability(
                getIndex(test1), getIndex(test2));
    }

    /**
     * The Tukey-Kramer honest significant difference test finds means that are
     * significantly different from each other. Consequently it can find means
     * that are significantly equal or not comparable (without enough data
     * to be statistically significant).
     *
     * @see https://en.wikipedia.org/wiki/Tukey%27s_range_test
     * @return the Tukey-Kramer honest significant difference (HSD) test value
     *         between the two tests with given indexes.
     */
    public double getTukeyKramerHsdConfidenceProbability(int index1, int index2) {
        return tukeyKramerConfidenceMatrix[index1][index2];
    }

    /**
     * @param testName
     * @return the index of the test
     */
    public int getIndex(String testName) {
        int index = 0;
        for (String name : testPerformance.keySet()) {
            if (name.equals(testName)) {
                return index;
            }
            index++;
        }
        throw new IllegalArgumentException("test not found = " + testName);
    }

    /**
     * @param index
     * @return the name of the test
     */
    public String getName(int index) {
        int i = 0;
        for (String name : testPerformance.keySet()) {
            if (i == index) {
                return name;
            }
            i++;
        }
        throw new IllegalArgumentException("invalid index = " + index);
    }

    private static double calculateMaxPercentageMargin(
            Collection<TestPerformance> testPerformances) {
        double max = Double.NEGATIVE_INFINITY;
        for (TestPerformance tp : testPerformances) {
            double margin = tp.getPercentage().getMarginOfError();
            if (margin > max) {
                max = margin;
            }
        }
        return max;
    }

    private static double[][] calculateTukeyKramerConfidenceMatrix(
            MultipleMeasure multiMeasure) {
        int count = multiMeasure.getMeasureCount();
        double[][] matrix = new double[count][count];
        for (int i=0; i<count; i++) {
            for (int j=i+1; j<count; j++) {
                double tukey = multiMeasure.tukeyKramerHsdPValue(i, j);
                matrix[i][j] = tukey;
                matrix[j][i] = tukey;
            }
        }
        return matrix;
    }

    /**
     * @param matrix Tukey's HSD test matrix between all test pairs.
     * @return the minimum significance probability between all the tests
     *         pairs.
     */
    private static double calculateMinTukeyHsdSignificanceProbability(
            double[][] matrix) {
        double min = Double.POSITIVE_INFINITY;
        int count = matrix.length;
        for (int i=0; i<count; i++) {
            for (int j=i+1; j<count; j++) {
                double tukey =
                        MultipleMeasure.significanceProbability(matrix[i][j]);
                if (tukey < min) {
                    min = tukey;
                }
            }
        }
        return min;
    }

    private long calculateGlobalTime(
            Map<String, TestPerformance> testPerformance) {
        long totalTimeAccumulator = 0;
        for (TestPerformance tp : testPerformance.values()) {
            totalTimeAccumulator += tp.getTotalTime();
        }
        return totalTimeAccumulator;
    }

    @Override
    public String toString() {
        return StringTableStatsFormatter.INSTANCE.toString(this);
    }
}
