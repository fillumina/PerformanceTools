package com.fillumina.performance.speed.stats;

import com.fillumina.performance.speed.stats.strgen.SpeedStatsTableStringGenerator;
import com.fillumina.performance.util.ValueAssertion;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultipleMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.fillumina.performance.assertion.AssertableMultiStats;

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
public class SpeedStats implements AssertableMultiStats, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, TestPerformance> testPerformance;
    private final MultipleMeasure multiMeasure;
    private final List<SpeedRatio> ratioList;
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
    public SpeedStats(OnlineMeasure global,
            MultipleMeasure multimeasure,
            Map<String, TestPerformance> testPerformance) {
        ValueAssertion.isNotNull(global, "global");
        ValueAssertion.isNotNull(multimeasure, "multimeasure");
        ValueAssertion.isNotNull(testPerformance, "testPerformance");

        this.multiMeasure = multimeasure;
        this.testPerformance = testPerformance;

        this.totalTime = calculateGlobalTime(testPerformance);
        this.ratioList = calculateRatios(multiMeasure, testPerformance);
        this.minTukeyKramerConfidence = calculateMinTukeyHsd(ratioList);

        this.maxPercentageMargin =
                calculateMaxPercentageMargin(testPerformance.values());
    }

    /** @return detailed statistics for each tests in the experiment. */
    public Map<String, TestPerformance> getPerformances() {
        return testPerformance;
    }

    @Override
    public Measure getValue(String testName) {
        return getPerformance(testName);
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName) {
        return testPerformance.get(testName).getRatio();
    }

    public List<SpeedRatio> getRatioList() {
        return ratioList;
    }

    /** @return the mean of the elapsed ns per cycle. */
    public Measure getPerformance(String testName)
            throws IllegalStateException {
        try {
            return testPerformance.get(testName).getElapsedNanosecondsPerCycle();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(
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
            return MultipleMeasure.significanceProbability(
                    minTukeyKramerConfidence);
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

    private static double calculateMaxPercentageMargin(
            Collection<TestPerformance> testPerformances) {
        double max = Double.NEGATIVE_INFINITY;
        for (TestPerformance tp : testPerformances) {
            double margin = tp.getRatio().getMarginOfError();
            if (margin > max) {
                max = margin;
            }
        }
        return max;
    }

    private static List<SpeedRatio> calculateRatios(
            MultipleMeasure multiMeasure,
            Map<String, TestPerformance> testPerformance) {
        List<TestPerformance> list = new ArrayList<>(testPerformance.values());
        int count = testPerformance.size();
        SpeedRatio[] array = new SpeedRatio[(count - 1) * (count)/ 2];
        int index=0;
        for (int i=0; i<count; i++) {
            for (int j=i+1; j<count; j++) {
                double tukey = multiMeasure.tukeyKramerHsdPValue(i, j);
                final TestPerformance t1 = list.get(i);
                Measure m1 = t1.getElapsedNanosecondsPerCycle();
                final TestPerformance t2 = list.get(j);
                Measure m2 = t2.getElapsedNanosecondsPerCycle();
                MeasureRatio directRatio = new MeasureRatio(m2, m1, 0.99);
                MeasureRatio inverseRatio = new MeasureRatio(m1, m2, 0.99);
                if (m1.getMean() > m2.getMean()) {
                    array[index] = new SpeedRatio(t2.getName(), t1.getName(),
                                        directRatio, inverseRatio, tukey);
                } else {
                    array[index] = new SpeedRatio(t1.getName(), t2.getName(),
                                        inverseRatio, directRatio, tukey);
                }
                index++;
            }
        }
        return Arrays.asList(array);
    }

    /**
     * @param ratios Tukey's HSD test matrix between all test pairs.
     * @return the minimum significance probability between all the tests
     *         pairs.
     */
    private static double calculateMinTukeyHsd(List<SpeedRatio> ratios) {
        if (ratios.isEmpty()) {
            return 1.0;
        }
        double min = Double.POSITIVE_INFINITY;
        for (SpeedRatio pr : ratios) {
            double tukey = pr.getTukeyHSD();
            if (tukey < min) {
                min = tukey;
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
        return SpeedStatsTableStringGenerator.INSTANCE.toString(this);
    }
}
