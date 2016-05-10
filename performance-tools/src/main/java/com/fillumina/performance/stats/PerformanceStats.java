package com.fillumina.performance.stats;

import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureDifference;
import com.fillumina.performance.util.stats.MultipleMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * It reads {@link PerformanceSample} and calculates
 * average performances and maximum standard deviation.
 *
 * @author Francesco Illuminati
 */
public class PerformanceStats implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String BASELINE_TEST_NAME = "[BASELINE]";
    public static final PerformanceStats EMPTY = new PerformanceStats();

    private final String message;
    private final Map<String, TestPerformances> testPerformance;
    private final MultipleMeasure multiMeasure;
    private final Measure baseline;
    private final double[][] tukeyKramerConfidenceMatrix;
    private final double minTukeyKramerConfidence;
    private final double maxPercentageMargin;
    private final double totalTime;
    private final double confidence;

    public static PerformanceStats copyWithNewMessage(PerformanceStats old,
            String message) {
        return new PerformanceStats(old, message);
    }

    /** private empty constructor */
    private PerformanceStats() {
        message = "EMPTY";
        testPerformance = Collections.<String,TestPerformances>emptyMap();
        multiMeasure = MultipleMeasure.EMPTY;
        totalTime = 0;
        tukeyKramerConfidenceMatrix = new double[0][0];
        minTukeyKramerConfidence = 0;
        maxPercentageMargin = 0;
        confidence = 0;
        baseline = OnlineMeasure.EMPTY;
    }

    public PerformanceStats(IterationRunningMeasure single,
            final double confidence) {
        message = "SINGLE";
        totalTime = single.getSum();
        List<IterationRunningMeasure> measures =
                Collections.singletonList(single);
        baseline = findBaseline(measures);
        testPerformance = createMap(measures, baseline, confidence);
        multiMeasure = new MultipleMeasure(single, new OnlineMeasure[]{single});
        tukeyKramerConfidenceMatrix =
                calculateTukeyKramerConfidenceMatrix(multiMeasure);
        minTukeyKramerConfidence = calculateMinTukeyHsdEvaluationPercentage(
                tukeyKramerConfidenceMatrix);
        maxPercentageMargin =
                calculateMaxPercentageMargin(testPerformance.values());
        this.confidence = confidence;
    }

    public PerformanceStats(String message,
            OnlineMeasure global,
            List<IterationRunningMeasure> measures,
            double confidence) {
        this.message = message;
        totalTime = global.getSum();
        multiMeasure = new MultipleMeasure(global,
                measures.toArray(new OnlineMeasure[measures.size()]));
        baseline = findBaseline(measures);
        testPerformance = createMap(measures, baseline, confidence);
        tukeyKramerConfidenceMatrix =
                calculateTukeyKramerConfidenceMatrix(multiMeasure);
        minTukeyKramerConfidence = calculateMinTukeyHsdEvaluationPercentage(
                tukeyKramerConfidenceMatrix);
        maxPercentageMargin =
                calculateMaxPercentageMargin(testPerformance.values());
        this.confidence = confidence;
    }

    /** Copy constructor. */
    private PerformanceStats(PerformanceStats other, String message) {
        this.message = message;
        this.testPerformance = other.testPerformance;
        this.multiMeasure = other.multiMeasure;
        this.baseline = other.baseline;
        this.tukeyKramerConfidenceMatrix = other.tukeyKramerConfidenceMatrix;
        this.minTukeyKramerConfidence = other.minTukeyKramerConfidence;
        this.maxPercentageMargin = other.maxPercentageMargin;
        this.totalTime = other.totalTime;
        this.confidence = other.confidence;
    }

    public Map<String, TestPerformances> getTestPerformances() {
        return testPerformance;
    }

    public String getMessage() {
        return message;
    }

    public Measure getBaseline() {
        return baseline;
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

    public double getConfidence() {
        return confidence;
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
        int index = 0, index1 = -1, index2 = -1;
        for (String name : testPerformance.keySet()) {
            if (index1 == -1 && name.equals(test1)) {
                index1 = index;
            }
            if (index2 == -1 && name.equals(test2)) {
                index2 = index;
            }
            if (index1 != -1 && index2 != -1) {
                break;
            }
            index++;
        }
        return tukeyKramerConfidenceMatrix[index1][index2];
    }

    private static double calculateMaxPercentageMargin(
            Collection<TestPerformances> testPerformances) {
        double max = Double.NEGATIVE_INFINITY;
        for (TestPerformances tp : testPerformances) {
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

    private Map<String, TestPerformances> createMap(
            final List<IterationRunningMeasure> measures,
            final Measure baseline,
            final double confidence) {
        if (measures.isEmpty()) {
            return Collections.<String, TestPerformances>emptyMap();
        }

        int slowIdx = getSlowerIndex(measures);
        Measure slowerMeasure = measures.get(slowIdx);
        Measure slower = baseline != null ?
                new MeasureDifference(slowerMeasure, baseline) :
                slowerMeasure;

        final Map<String, TestPerformances> localMap =
                new LinkedHashMap<>(measures.size());
        int index = 0;
        for (IterationRunningMeasure measure : measures) {
            if (measure != baseline) {
                Measure corrected = baseline != null ?
                        new MeasureDifference(measure, baseline) :
                        measure;
                TestPerformances tp = new TestPerformances(
                        measure.getName(),
                        corrected,
                        slower,
                        confidence,
                        tukey(index, slowIdx),
                        measure.getIterations(),
                        measure.getTotalTime());
                localMap.put(measure.getName(), tp);
                index++;
            }
        }
        return Collections.unmodifiableMap(localMap);
    }

    private double tukey(int index, final int slowIdx) {
        if (index == slowIdx) {
            return 1.0;
        }
        return multiMeasure.tukeyKramerHsdPValue(index, slowIdx);
    }

    private Measure findBaseline(final List<IterationRunningMeasure> measures) {
        for (IterationRunningMeasure irm : measures) {
            if (BASELINE_TEST_NAME.equals(irm.getName())) {
                return irm;
            }
        }
        return null;
    }

    private int getSlowerIndex(List<IterationRunningMeasure> measures) {
        double mean, slower = Double.NEGATIVE_INFINITY;
        int index = 0, slowerIndex = -1;
        for (IterationRunningMeasure m : measures) {
            if (!BASELINE_TEST_NAME.equals(m.getName())) {
                mean = m.getMean();
                if (mean > slower) {
                    slower = mean;
                    slowerIndex = index;
                }
                index++;
            }
        }
        return slowerIndex;
    }

    @Override
    public String toString() {
        return StringTableStatsViewer.getTable(null, this).toString();
    }
}
