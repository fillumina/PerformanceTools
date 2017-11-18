package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.Printable;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.DefaultDimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.io.IOException;
import java.io.Serializable;
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
public class Stats<T extends SingleStats>
        extends Printable<Stats<T>>
        implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    private final ReferenceMeasure<T> refMeasure;
    private final TNameMap<T> map;
    private final MultiMeasureSignificance multiMeasure;

    private TukeyPrintable tukeyPrintable;

    /** Copy constructor. */
    public Stats(Stats<T> stats) {
        this(stats.multiMeasure, stats.map);
    }

    /**
     *
     * @param global            all samples statistics together (used for ANOVA)
     * @param multiMeasure      multiple measure statistics (ANOVA)
     * @param testStatsMap      statistics for each test independently
     */
    public Stats(MultiMeasureSignificance multiMeasure,
            TNameMap<T> singleStatsMap) {
        this.map = new TNameMap<>(singleStatsMap);
        this.refMeasure = new ReferenceMeasure<>(singleStatsMap.values());
        this.multiMeasure = multiMeasure;
    }

    protected static class Joiner<T extends SingleStats> {
        private final MultiMeasureSignificance multiMeasure;
        private final TNameMap<T> map = new TNameMap<>();

        public Joiner(Stats<T> a, Stats<T> b) {
            multiMeasure =
                MultiMeasureSignificance.join(a.multiMeasure, b.multiMeasure);
            map.putAll(a.map);
            map.putAll(b.map);
        }

        public MultiMeasureSignificance getMultiMeasure() {
            return multiMeasure;
        }

        public TNameMap<T> getMap() {
            return map;
        }
    }

    // TODO override
    /** Must be overridden by subclasses. */
    public Stats<T> createNewAdding(Map<TName, Measure> measures) {
        Unit<?> unit = getSingleStatsMap().values().iterator().next()
                .getMeasure().getUnit();
        TNameMap<T> measureMap = new TNameMap<>();
        Holder.Integer index = new Holder.Integer();
        measures.forEach((TName name, Measure measure) -> {
            measureMap.add((T)new SingleStats(name,
                    new DefaultDimensionalMeasure(measure, unit)) );
        });
        Measure[] array = (Measure[]) measures.values().toArray();
        MultiMeasureSignificance mms =
                MultiMeasureSignificance.createFrom(array);
        Stats<T> created = new Stats<>(mms, measureMap);
        return join(created);
    }

    /** Must be overridden by subclasses. */
    public Stats<T> join(Stats<T> other) {
        Joiner<T> joiner = new Joiner<>(this, other);
        return new Stats<>(joiner.getMultiMeasure(), joiner.getMap());
    }

    public TNameMap<T> getSingleStatsMap() {
        return map.unmodifiable();
    }

    @Override
    public Measure getMeasure(CharSequence testName)
            throws IllegalStateException {
        SingleStats single = map.get(testName);
        if (single == null) {
            throw new TestNotFoundException(testName, map.keySet());
        }
        return single.getMeasure();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public List<TName> getNames() {
        return map.keyList();
    }

    public MeasureRatio getRatio(CharSequence testName1, CharSequence testName2,
            Ratio confidence) {
        Measure one = getMeasure(testName1);
        Measure two = getMeasure(testName2);
        return new MeasureRatio(one, two, confidence);
    }

    // see MeasureRatioCalculator
    public MeasureRatio getRatio(CharSequence testName, Ratio confidence) {
        Measure m = getMeasure(testName);
        if (m == null) {
            throw new TestNotFoundException(testName, getNames());
        }
        Measure ref = getMeasure(getReferenceMeasureName());
        return new MeasureRatio(m, ref, confidence);
    }

    /**
     * Calculates an estimation that the pair of means are significantly
     * different from each other.
     *
     * @param testName1 name of the first test
     * @param testName2 name of the second test
     * @return the Tukey's Honest Significant Difference
     */
    public double getTukeyHsd(CharSequence testName1, CharSequence testName2) {
        int idx1 = getIndexOf(testName1);
        int idx2 = getIndexOf(testName2);
        return multiMeasure.tukeyKramerHsdPValue(idx1, idx2);
    }

    public double getTukeyHsdComparedToRef(CharSequence testName) {
        int idx1 = getIndexOf(testName);
        return multiMeasure.tukeyKramerHsdPValue(idx1,
                refMeasure.getReferenceTestIndex());
    }

    /**
     * @return the higher margin of error of the ratios of each measure
     *         in the experiment confronted with the slower one. It's an
     *         estimation of the accuracy of the experiment.
     */
    public Ratio getMaximumPercentageMargin(Ratio confidence) {
        double max = 0;
        for (CharSequence name : getNames()) {
            double moe = getRatio(name, confidence).getMarginOfError();
            if (moe > max) {
                max = moe;
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

    public TName getReferenceMeasureName() {
        return refMeasure.getReferenceTestName();
    }

    private int getIndexOf(CharSequence testName) {
        String testNameString = testName.toString();
        List<TName> names = getNames();
        for (int i=0, l=names.size(); i<l; i++) {
            CharSequence c = names.get(i);
            if (c.equals(testName) || c.toString().equals(testNameString)) {
                return i;
            }
        }
        throw new TestNotFoundException(testName, map.keySet());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.map);
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
        final Stats<?> other = (Stats<?>) obj;
        if (!Objects.equals(this.map, other.map)) {
            return false;
        }
        return true;
    }

    @Override
    public Stats<T> appendTo(Appendable appendable) {
        try {
            StatsTableStringGenerator.INSTANCE.appendTo(appendable, this);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    // TODO test this
    public Printable<?> getPrintableTukeyMatrix() {
        if (tukeyPrintable == null) {
            tukeyPrintable = new TukeyPrintable(this);
        }
        return tukeyPrintable;
    }

    private static class TukeyPrintable extends Printable<TukeyPrintable> {
        private final Stats<?> stats;

        public TukeyPrintable(Stats<?> stats) {
            this.stats = stats;
        }

        @Override
        public TukeyPrintable appendTo(Appendable appendable) {
            try {
                TukeyMatrixStringGenerator.INSTANCE.appendTo(appendable, stats);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return this;
        }
    }
}
