package com.fillumina.performance.speed.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.ValueAssertion;
import com.fillumina.performance.util.collection.SymmetricMatrix;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.fillumina.performance.util.collection.UnmodifiableSymmetricMatrix;

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
//TODO return SymmetricMatrix
public class SpeedStats implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    private final MultiMeasure multiMeasure;
    private final Map<String, SingleSpeedStats> testStatsMap;
    private final UnmodifiableSymmetricMatrix<String, SpeedRatio> ratioMap;
    private final String slowestTestName;
    private final double minTukeyKramerConfidence;
    private final long totalTime;

    /**
     *
     * @param global            all samples statistics together (used for ANOVA)
     * @param multiMeasure      multiple measure statistics (ANOVA)
     * @param testStatsMap      statistics for each test independently
     */
    public SpeedStats(OnlineMeasure global,
            MultiMeasure multiMeasure,
            LinkedHashMap<String, SingleSpeedStats> testStatsMap) {
        ValueAssertion.isNotNull(global, "global");
        ValueAssertion.isNotNull(multiMeasure, "multimeasure");
        ValueAssertion.isNotNull(testStatsMap, "testStatsMap");

        this.multiMeasure = multiMeasure;
        this.testStatsMap = Collections.unmodifiableMap(testStatsMap);

        this.totalTime = calculateTotalTime(testStatsMap);
        this.slowestTestName = findSlowestTestName(testStatsMap);
        this.ratioMap = calculateRatioMap(multiMeasure, testStatsMap);
        this.minTukeyKramerConfidence = calculateMinTukeyHsd(ratioMap.values());
    }

    public boolean isEmpty() {
        return testStatsMap.isEmpty();
    }

    /** @return detailed statistics for each tests in the experiment. */
    public Map<String, SingleSpeedStats> getPerformanceMap() {
        return testStatsMap;
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName,
            Ratio confidence) {
        SpeedRatio ratio = ratioMap.get(testName, slowestTestName);
        if (ratio == null) {
            throw createTestNotFoundException(testName);
        }
        return ratio.getRatio(confidence);
    }

    /** @return the measure of the elapsed nanoseconds per cycle. */
    @Override
    public Measure getValue(String testName)
            throws IllegalStateException {
        SingleSpeedStats single = testStatsMap.get(testName);
        if (single == null) {
            throw createTestNotFoundException(testName);
        }
        return single.getElapsedNanosecondsPerCycle();
    }

    private IllegalArgumentException createTestNotFoundException(
            String testName) {
        return new IllegalArgumentException("Test '" + testName +
                        "' not found, valid tests are: " +
                        testStatsMap.keySet().toString());
    }

    public double getTukeyHsd(String testName) {
        if (testName.equals(slowestTestName)) {
            return 1.0;
        }
        return ratioMap.get(testName, slowestTestName).getTukeyHSD();
    }

    /**
     * @return the total time spent performing the experiment (in nanoseconds).
     */
    public long getTotalTimeNs() {
        return totalTime;
    }

    /**
     * @return the higher margin of confidence of the ratios of each measure
     *         in the experiment confronted with the slower one. It's an
     *         estimation of the accuracy of the experiment.
     */
    public double getMaximumPercentageMargin(Ratio confidence) {
        return calculateMaxPercentageMargin(ratioMap.values(), confidence);
    }

    /**
     * ANOVA (Analysis of Variance) provides a statistical test of whether
     * or not the means of several groups are equal. This probability is close
     * to 1 if at least one pair of means are equal and is close to 0 if all
     * the means are different between each other. If the probability is around
     * 0.5 it means that there aren't enough data (samples or internal variance)
     * to provide an answer. This characteristic can be used by algorithms to
     * evaluate if the experiment needs to be repeated with an increased
     * precision (i.e. more iterations/ samples).
     *
     * @see <a href='https://en.wikipedia.org/wiki/Analysis_of_variance'>
     *  Wikipedia: Analysis of Variance (ANOVA)</a>
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
    public double getMinTukeyHsd() {
        return minTukeyKramerConfidence;
    }

    /**
     * Calculates the ratios between experiments so to evaluate the
     * relative speed between each of them.
     *
     * @param confidence the required confidence
     * @return a list of ratio between pairs of experiments
     */
    public Collection<SpeedRatio> getRatioList() {
        return ratioMap.values();
    }

    static double calculateMaxPercentageMargin(
            Collection<SpeedRatio> ratios,
            Ratio confidence) {
        double max = Double.NEGATIVE_INFINITY;
        for (SpeedRatio ratio : ratios) {
            double margin = ratio.getRatio(confidence).getMarginOfError();
            if (margin > max) {
                max = margin;
            }
        }
        return max;
    }

    static String findSlowestTestName(
            Map<String, SingleSpeedStats> testStatsMap) {
        String slowestName = null;

        double slowestMean = -1;
        for (Entry<String,SingleSpeedStats> entry : testStatsMap.entrySet()) {
            SingleSpeedStats single = entry.getValue();
            double mean = single.getElapsedNanosecondsPerCycle().getMean();
            if (slowestMean == -1 || slowestMean < mean) {
                slowestName = single.getName();
                slowestMean = mean;
            }
        }
        return slowestName;
    }

    static SymmetricMatrix<String, SpeedRatio> calculateRatioMap(
            MultiMeasure multiMeasure,
            Map<String, SingleSpeedStats> testStatsMap) {

        List<SingleSpeedStats> list = new ArrayList<>(testStatsMap.values());
        final int listSize = list.size();

        String[] names = testStatsMap.keySet().toArray(
                        new String[testStatsMap.size()]);
        SymmetricMatrix<String, SpeedRatio> map = new SymmetricMatrix<>(names);

        for (int i=0; i<listSize; i++) {
            SingleSpeedStats test1 = list.get(i);
            for (int j=0; j<=i; j++) {
                SingleSpeedStats test2 = list.get(j);

                double tukey = multiMeasure.tukeyKramerHsdPValue(i, j);
                SpeedRatio ratio = new SpeedRatio(
                        test1.getName(), test1.getElapsedNanosecondsPerCycle(),
                        test2.getName(), test2.getElapsedNanosecondsPerCycle(),
                        tukey);

                map.putByIndex(i, j, ratio);
            }
        }
        return map;
    }

    /**
     * Finds the minimum value of the Tukey HSD over all pairs
     * of experiments. It's an evaluation about the quality of the
     * samples taken.
     *
     * @param ratios Tukey's HSD test matrix between all test pairs.
     * @return the minimum significance probability between all the tests
     *         pairs.
     */
    static double calculateMinTukeyHsd(Collection<SpeedRatio> ratios) {
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

    static long calculateTotalTime(Map<String, SingleSpeedStats> testStatsMap) {
        long totalTimeAccumulator = 0;
        for (SingleSpeedStats tp : testStatsMap.values()) {
            totalTimeAccumulator += tp.getTotalTime();
        }
        return totalTimeAccumulator;
    }

    @Override
    public String toString() {
        return WrapperSpeedStatsTableStringGenerator.INSTANCE.toString(
                new PHolder<>(this));
    }
}
