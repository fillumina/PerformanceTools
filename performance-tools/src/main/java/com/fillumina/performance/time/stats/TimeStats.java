package com.fillumina.performance.time.stats;

import com.fillumina.performance.assertion.AbstractAssertable;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.time.stats.strgen.SpeedStatsTukeyMatrixStringGenerator;
import com.fillumina.performance.time.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.UnmodificableTNameMapWrapper;
import com.fillumina.performance.util.ValueAssertion;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public class TimeStats extends AbstractAssertable
        implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    private final MultiMeasure multiMeasure;
    private final UnmodificableTNameMapWrapper<SingleTimeStats> testStatsMap;
    private final Map<TName, Integer> indexes;

    public static <T extends TimeStats> T joinAll(T... timeStats) {
        return joinAll(Arrays.asList(timeStats));
    }

    @SuppressWarnings("unchecked")
    public static <T extends TimeStats> T joinAll(List<T> stats) {
        switch (stats.size()) {
            case 0:
                return null;

            case 1:
                return stats.get(0);

            case 2:
                return (T) stats.get(0).join(stats.get(1));

            default:
                T accumulator = stats.get(0);
                for (int i=1; i<stats.size(); i++) {
                    accumulator = (T) accumulator.join(stats.get(i));
                }
                return accumulator;
        }
    }

    public TimeStats add(SingleTimeStats single) {
        MultiMeasure jointMm = MultiMeasure.add(getMultiMeasure(),
                single.getElapsedNanosecondsPerCycle());
        LinkedHashMap<TName,SingleTimeStats> map = new LinkedHashMap<>();
        map.putAll(getTestStatsMap());
        map.put(single.getName(), single);
        return new TimeStats(jointMm, map);
    }

    public TimeStats join(TimeStats b) {
        MultiMeasure jointMm =
                MultiMeasure.join(getMultiMeasure(), b.getMultiMeasure());
        LinkedHashMap<TName,SingleTimeStats> map = new LinkedHashMap<>();
        map.putAll(getTestStatsMap());
        map.putAll(b.getTestStatsMap());
        return new TimeStats(jointMm, map);
    }

    protected MultiMeasure getMultiMeasure() {
        return multiMeasure;
    }

    protected UnmodificableTNameMapWrapper<SingleTimeStats> getTestStatsMap() {
        return testStatsMap;
    }

    /**
     *
     * @param global            all samples statistics together (used for ANOVA)
     * @param multiMeasure      multiple measure statistics (ANOVA)
     * @param testStatsMap      statistics for each test independently
     */
    public TimeStats(MultiMeasure multiMeasure,
            LinkedHashMap<TName, SingleTimeStats> testStatsMap) {
        ValueAssertion.isNotNull(multiMeasure, "multimeasure");
        ValueAssertion.isNotNull(testStatsMap, "testStatsMap");

        this.multiMeasure = multiMeasure;
        this.testStatsMap = new UnmodificableTNameMapWrapper<>(testStatsMap);
        this.indexes = calculateIndexes(testStatsMap);
    }

    /** @return detailed statistics for each tests in the experiment. */
    public UnmodificableTNameMapWrapper<SingleTimeStats> getSingleStatsMap() {
        return testStatsMap;
    }

    @Override
    public Collection<TName> getTestNames() {
        return testStatsMap.keySet();
    }

    /** @return the measure of the elapsed nanoseconds per cycle. */
    @Override
    public Measure getMeasure(TName testName)
            throws IllegalStateException {
        SingleTimeStats single = testStatsMap.get(testName);
        if (single == null) {
            throw new TestNotFoundException(testName, testStatsMap.keySet());
        }
        return single.getElapsedNanosecondsPerCycle();
    }

    public MeasureRatio getRatio(String testName1, String testName2,
            Ratio confidence) {
        return getRatio(TN.tname(testName1), TN.tname(testName2), confidence);
    }

    public MeasureRatio getRatio(TName testName1, TName testName2,
            Ratio confidence) {
        Measure one = getMeasure(testName1);
        Measure two = getMeasure(testName2);
        return new MeasureRatio(one, two, confidence);
    }

    /**
     * Calculates an estimation that the pair of means are significantly
     * different from each other.
     *
     * @param testName1 name of the first test
     * @param testName2 name of the second test
     * @return the Tukey's Honest Significant Difference
     */
    public double getTukeyHsd(String testName1, String testName2) {
        return getTukeyHsd(TN.tname(testName1), TN.tname(testName2));
    }

    public double getTukeyHsd(TName testName1, TName testName2) {
        int idx1 = getIndexOf(testName1);
        int idx2 = getIndexOf(testName2);
        return multiMeasure.tukeyKramerHsdPValue(idx1, idx2);
    }

    public double getTukeyHsdComparedToSlowest(String testName) {
        return getTukeyHsdComparedToSlowest(TN.tname(testName));
    }

    public double getTukeyHsdComparedToSlowest(TName testName) {
        int idx1 = getIndexOf(testName);
        return multiMeasure.tukeyKramerHsdPValue(idx1, getSlowestTestIndex());
    }

    /**
     * @return the total time spent performing the experiment (in nanoseconds).
     */
    public long getTotalTimeNs() {
        long totalTimeAccumulator = 0;
        for (SingleTimeStats tp : testStatsMap.values()) {
            totalTimeAccumulator += tp.getTotalTime();
        }
        return totalTimeAccumulator;
    }

    /**
     * @return the higher margin of error of the ratios of each measure
     *         in the experiment confronted with the slower one. It's an
     *         estimation of the accuracy of the experiment.
     */
    public Ratio getMaximumPercentageMargin(Ratio confidence) {
        TName slowestName = getSlowestTestName();
        double max = 0;
        for (TName name : getTestNames()) {
            if (!name.equals(slowestName)) {
                double moe = getRatioWithSlowestTest(name, confidence)
                        .getMarginOfError();
                if (moe > max) {
                    max = moe;
                }
            }
        }
        return Ratio.decimal(max);
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
     * Finds the minimum value of the Tukey HSD over all pairs
     * of experiments.
     * It's an estimation of the statistical significance of the collected data.
     * The Tukey HSD can be performed only if ANOVA is either close to 0
     * or to 1.
     *
     * @return the minimum value of the Tukey HSD test appied to all test
     *         pairs.
     */
    public double getMinTukeyHsd() {
        double min = Double.POSITIVE_INFINITY;
        int size = multiMeasure.getMeasureCount();
        for (int i=0; i<size; i++) {
            for (int j=0; j<=i; j++) {
                double tukey = multiMeasure.tukeyKramerHsdPValue(i, j);
                if (tukey < min) {
                    min = tukey;
                }
            }
        }
        return min;
    }

    private int getIndexOf(TName testName) {
        Integer idx = indexes.get(testName);
        if (idx == null) {
            throw new TestNotFoundException(testName, testStatsMap.keySet());
        }
        return idx;
    }

    static Map<TName, Integer> calculateIndexes(
            LinkedHashMap<TName, SingleTimeStats> testStatsMap) {
        Map<TName,Integer> indexMap = new LinkedHashMap<>(testStatsMap.size());
        int index = 0;
        for (TName name : testStatsMap.keySet()) {
            indexMap.put(name, index);
            index++;
        }
        return indexMap;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.multiMeasure);
        hash = 79 * hash + Objects.hashCode(this.testStatsMap);
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
        final TimeStats other = (TimeStats) obj;
        if (!Objects.equals(this.multiMeasure, other.multiMeasure)) {
            return false;
        }
        if (!Objects.equals(this.testStatsMap, other.testStatsMap)) {
            return false;
        }
        return true;
    }

    public String getTukeyMatrix() {
        return SpeedStatsTukeyMatrixStringGenerator.INSTANCE.toString(this);
    }

    @Override
    public String toString() {
        return WrapperSpeedStatsTableStringGenerator.INSTANCE.toString(this);
    }
}
