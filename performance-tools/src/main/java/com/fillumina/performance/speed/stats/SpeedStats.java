package com.fillumina.performance.speed.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.ValueAssertion;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class SpeedStats implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final OnlineMeasure ZERO = new OnlineMeasure(0);

    private final Map<String, SingleSpeedStats> testStatsMap;
    private final MultiMeasure multiMeasure;
    private final List<SpeedRatio> ratioList;
    private final Map<String, SpeedRatio> ratioMap;
    private final double minTukeyKramerConfidence;
    private final double maxPercentageMargin;
    private final long totalTime;

    /**
     *
     * @param global            all samples statistics together (used for ANOVA)
     * @param multiMeasure      multiple measure statistics (ANOVA)
     * @param testStatsMap   statistics for each test
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
        this.ratioMap = calculateRatioMap(multiMeasure, testStatsMap);
        this.ratioList = calculateRatios(multiMeasure, testStatsMap);
        this.minTukeyKramerConfidence = calculateMinTukeyHsd(ratioList);
        this.maxPercentageMargin =
                calculateMaxPercentageMargin(ratioMap, Ratio.P_95);
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
        return ratioMap.get(testName).getRatio(confidence);
    }

    /** @return the measure of the elapsed nanoseconds per cycle. */
    @Override
    public Measure getValue(String testName)
            throws IllegalStateException {
        try {
            return testStatsMap.get(testName).getElapsedNanosecondsPerCycle();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(
                    "Test '" + testName +
                    "' not found, valid tests are: " +
                    testStatsMap.keySet().toString(), e);
        }
    }

    public double getTukeyHsd(String testName) {
        return ratioMap.get(testName).getTukeyHSD();
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
    public double getMaximumPercentageMargin() {
        return maxPercentageMargin;
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
    public List<SpeedRatio> getRatioList() {
        return ratioList;
    }

    static double calculateMaxPercentageMargin(
            Map<String, SpeedRatio> ratioMap,
            Ratio confidence) {
        double max = Double.NEGATIVE_INFINITY;
        for (SpeedRatio ratio : ratioMap.values()) {
            double margin = ratio.getRatio(confidence).getMarginOfError();
            if (margin > max) {
                max = margin;
            }
        }
        return max;
    }

    static Map<String, SpeedRatio> calculateRatioMap(
            MultiMeasure multiMeasure,
            Map<String, SingleSpeedStats> testStatsMap) {
        Measure slowestMeasure = ZERO;
        String slowestName = null;
        int slowestIndex = -1;

        // finds slowest test
        int index = 0;
        for (Entry<String,SingleSpeedStats> entry : testStatsMap.entrySet()) {
            SingleSpeedStats tp = entry.getValue();
            Measure m = tp.getElapsedNanosecondsPerCycle();
            if (slowestMeasure.getMean() < m.getMean()) {
                slowestMeasure = m;
                slowestIndex = index;
                slowestName = entry.getKey();
            }
            index++;
        }

        // creates ratio map
        Map<String, SpeedRatio> map = new HashMap<>();
        index = 0;
        for (SingleSpeedStats tp : testStatsMap.values()) {
            Measure m = tp.getElapsedNanosecondsPerCycle();
            double tukey = multiMeasure.tukeyKramerHsdPValue(index, slowestIndex);
            String name = tp.getName();
            SpeedRatio sr =
                    new SpeedRatio(name, m, slowestName, slowestMeasure, tukey);
            map.put(name, sr);
            index++;
        }
        return Collections.unmodifiableMap(map);
    }

    /** Calculates a collection of the ratios of all possible experiments. */
    static List<SpeedRatio> calculateRatios(
            MultiMeasure multiMeasure,
            Map<String, SingleSpeedStats> testStatsMap) {
        List<SingleSpeedStats> list = new ArrayList<>(testStatsMap.values());
        int count = testStatsMap.size();
        SpeedRatio[] ratios = new SpeedRatio[(count - 1) * count / 2];
        int index=0;
        for (int i=0; i<count; i++) {
            for (int j=i+1; j<count; j++) {
                final SingleSpeedStats t1 = list.get(i);
                final SingleSpeedStats t2 = list.get(j);
                Measure m1 = t1.getElapsedNanosecondsPerCycle();
                Measure m2 = t2.getElapsedNanosecondsPerCycle();
                double tukey = multiMeasure.tukeyKramerHsdPValue(i, j);

                ratios[index] =
                        new SpeedRatio(t1.getName(), m1, t2.getName(), m2, tukey);

                index++;
            }
        }
        return Collections.unmodifiableList(Arrays.asList(ratios));
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
    static double calculateMinTukeyHsd(List<SpeedRatio> ratios) {
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
