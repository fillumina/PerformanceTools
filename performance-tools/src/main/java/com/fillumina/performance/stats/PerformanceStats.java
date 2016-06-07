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
 * It reads {@link PerformanceSample} and calculates
 * average performances and maximum standard deviation.
 *
 * @author Francesco Illuminati
 */
public class PerformanceStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final Map<String, TestPerformance> testPerformance;
    private final MultipleMeasure multiMeasure;
    private final double[][] tukeyKramerConfidenceMatrix;
    private final double minTukeyKramerConfidence;
    private final double maxPercentageMargin;
    private final double totalTime;

    public static PerformanceStats copyWithNewMessage(PerformanceStats old,
            String message) {
        return new PerformanceStats(old, message);
    }

    /** Copy constructor. */
    private PerformanceStats(PerformanceStats other, String message) {
        this.message = message;
        this.testPerformance = other.testPerformance;
        this.multiMeasure = other.multiMeasure;
        this.tukeyKramerConfidenceMatrix = other.tukeyKramerConfidenceMatrix;
        this.minTukeyKramerConfidence = other.minTukeyKramerConfidence;
        this.maxPercentageMargin = other.maxPercentageMargin;
        this.totalTime = other.totalTime;
    }

    /**
     *
     * @param message           error message
     * @param global            all samples statistics together (used for ANOVA)
     * @param multimeasure      multiple measure statistics (ANOVA)
     * @param testPerformance   statistics for each test
     * @param confidence        confidence
     */
    public PerformanceStats(String message,
            OnlineMeasure global,
            MultipleMeasure multimeasure,
            Map<String, TestPerformance> testPerformance) {
        Assertion.isNotNull(global, "global");
        Assertion.isNotNull(multimeasure, "multimeasure");
        Assertion.isNotNull(testPerformance, "testPerformance");

        this.message = message;
        this.totalTime = global.getSum();
        this.multiMeasure = multimeasure;
        this.testPerformance = testPerformance;

        if (isInvalid(testPerformance)) {
            // samples are almost coincidental
            this.tukeyKramerConfidenceMatrix = null;
            this.minTukeyKramerConfidence = 0.0;
        } else {
            this.tukeyKramerConfidenceMatrix =
                    calculateTukeyKramerConfidenceMatrix(multiMeasure);
            this.minTukeyKramerConfidence =
                    calculateMinTukeyHsdEvaluationPercentage(
                            tukeyKramerConfidenceMatrix);
        }

        this.maxPercentageMargin =
                calculateMaxPercentageMargin(testPerformance.values());
    }

    public Map<String, TestPerformance> getTestPerformances() {
        return testPerformance;
    }

    public String getMessage() {
        return message;
    }

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

    public double getTotalTime() {
        return totalTime;
    }

    public double getStatisticalSignificanceMatrixProbability() {
        final double anova = MultipleMeasure.significanceEvaluation(getAnova());
        if (anova > .9) {
            return getMinTukeyHsdEvaluationPercentage();
        }
        return anova;
    }

    public double getMaximumPercentageMargin() {
        return maxPercentageMargin;
    }

    public double getAnova() {
        return multiMeasure.anovaPValue();
    }

    public double getMinTukeyHsdEvaluationPercentage() {
        return minTukeyKramerConfidence;
    }

    public double getTukeyKramerHsdConfidenceProbability(
            String test1, String test2) {
        return getTukeyKramerHsdConfidenceProbability(
                getIndex(test1), getIndex(test2));
    }

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

    public double getTukeyKramerHsdConfidenceProbability(int index1, int index2) {
        return tukeyKramerConfidenceMatrix[index1][index2];
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

    private static double calculateMinTukeyHsdEvaluationPercentage(
            double[][] matrix) {
        double min = Double.POSITIVE_INFINITY;
        int count = matrix.length;
        for (int i=0; i<count; i++) {
            for (int j=i+1; j<count; j++) {
                double tukey =
                        MultipleMeasure.significanceEvaluation(matrix[i][j]);
                if (tukey < min) {
                    min = tukey;
                }
            }
        }
        return min;
    }

    @Override
    public String toString() {
        return StringTableStatsFormatter.INSTANCE.toString(this);
    }

    private boolean isInvalid(Map<String, TestPerformance> testPerformance) {
        for (TestPerformance tp : testPerformance.values()) {
            final Measure measure = tp.getElapsedNanosecondsPerCycle();
            if (Double.isNaN(measure.getUnbiasedVariance())) {
                return true;
            }
        }
        return false;
    }
}
