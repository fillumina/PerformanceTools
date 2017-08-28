package com.fillumina.performance.time.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.stats.Stats;
import com.fillumina.performance.util.UnmodificableTNameMapWrapper;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.tname.TName;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

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
public class TimeStats extends Stats<SingleTimeStats>
        implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    @SafeVarargs
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
                return (T) join(stats.get(0), stats.get(1));

            default:
                T accumulator = stats.get(0);
                for (int i=1; i<stats.size(); i++) {
                    accumulator = join(accumulator, stats.get(i));
                }
                return accumulator;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends TimeStats> T add(T t, SingleTimeStats single) {
        MultiMeasure jointMm = MultiMeasure.add(
                t.getMultiMeasure(),
                single.getMeasure());
        LinkedHashMap<TName,SingleTimeStats> map = new LinkedHashMap<>();
        map.putAll(t.getTestStatsMap());
        map.put(single.getName(), single);
        return (T) t.createNew(jointMm, map);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TimeStats> T join(T a, T b) {
        MultiMeasure jointMm =
                MultiMeasure.join(a.getMultiMeasure(), b.getMultiMeasure());
        LinkedHashMap<TName,SingleTimeStats> map = new LinkedHashMap<>();
        map.putAll(a.getTestStatsMap());
        map.putAll(b.getTestStatsMap());
        return (T) a.createNew(jointMm, map);
    }

    protected MultiMeasure getMultiMeasure() {
        return multiMeasure;
    }

    protected TimeStats createNew(MultiMeasure multiMeasure,
            LinkedHashMap<TName, SingleTimeStats> testStatsMap) {
        return new TimeStats(multiMeasure, testStatsMap);
    }

    protected UnmodificableTNameMapWrapper<SingleTimeStats> getTestStatsMap() {
        return testStatsMap;
    }

    public TimeStats(MultiMeasure multiMeasure,
            List<SingleTimeStats> singleStatsList) {
        super(multiMeasure, singleStatsList);
    }
}
