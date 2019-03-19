package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.executor.PN;
import com.fillumina.performance.util.Printable;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.QuantityList;
import com.fillumina.performance.util.unit.Unit;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * An {@link AssertableExperiment} representing the Statistics about an
 * experiment including various tests of the same type.
 * In addition of the usual statistics it calculates ANOVA and performs the
 * Games-Howell HSD post-hoc test on all experiment pairs so to assess the
 * statistic significance of results.
 * <p>
 * It is possible to add named payloads that contain configurable info about the
 * various stages of the creation of the stats.
 * <p>
 * This class is immutable.
 *
 * @author Francesco Illuminati
 */
public class Stats extends Printable<Stats>
        implements StatsTyped, AssertableExperiment, Serializable {
    private static final long serialVersionUID = 1L;

    /** Different type of statistics shouldn't be matched. */
    private final StatsType type;

    /** The results are presented in relation with the bigger value. */
    private final BiggerMeasure refMeasure;

    private final IndexedHashMap<PathName, DimensionalMeasure> map;
    private final MultiMeasureSignificance multiMeasure;
    private final Unit<?> unit;
    private final Map<String, Object> payloadMap;
    private final Predicate<String> filter;

    private SignificancePrintable significancePrintable;

    /** Copy constructor. */
    public Stats(Stats copy) {
        this(copy.type, copy.map, copy.unit, copy.payloadMap, copy.filter);
    }

    /** Copy constructor but with a new filter. */
    public Stats(Stats copy, Predicate<String> filter) {
        this(copy.type, copy.map, copy.unit, copy.payloadMap, filter);
    }

    /** Copy constructor but with new measures. */
    public Stats(Stats copy, Map<PathName,DimensionalMeasure> measures) {
        this(copy.type, measures, copy.unit, copy.payloadMap, copy.filter);
    }

    /** Simplest constructor using {@link StatsType#DEFAULT} stats type. */
    public Stats(Map<PathName,DimensionalMeasure> measures) {
        this(StatsType.DEFAULT, measures, getArmonizedUnit(measures.values()) );
    }

    /** Simplest constructor defining {@link StatsType}. */
    public Stats(StatsType type, Map<PathName,DimensionalMeasure> measures) {
        this(type, measures, getArmonizedUnit(measures.values()) );
    }

    /** Force a specific unit. */
    public Stats(StatsType type,
            Map<PathName,DimensionalMeasure> measures,
            Unit<?> unit) {
        this(type, measures, unit, null, null);
    }

    /**
     * Full blown constructor.
     *
     * @param type stats' type
     * @param measures the measures of the experiment
     * @param unit if null the unit is extracted from measures
     * @param payload various data not managed by the class
     * @param filter filter the tests that should be included in the report.
     *         Used by expressions to hide the original values.
     */
    public Stats(StatsType type,
            Map<PathName,DimensionalMeasure> measures,
            Unit<?> unit, // to force a specific unit, null to auto-select
            Map<String, Object> payloadMap,
            Predicate<String> filter) {
        Unit<?> armonizedUnit = getArmonizedUnit(measures.values());
        if (unit != null) {
            if (armonizedUnit != null && !unit.isSameType(armonizedUnit)) {
                throw new RuntimeException("given unit " + unit +
                        " is not compatible with used one " + armonizedUnit);
            }
            this.unit = unit;
            this.map = createNormalizedMap(measures, unit);
        } else {
            this.unit = armonizedUnit;
            this.map = createNormalizedMap(measures, this.unit);
        }
        this.type = type;
        this.refMeasure = filter == null ?
                new BiggerMeasure(this.map) : new BiggerMeasure(map, filter);
        this.multiMeasure = new MultiMeasureSignificance(measures.values());
        this.payloadMap = payloadMap == null ? new HashMap<>() :
                new HashMap<>(payloadMap);
        this.filter = filter;
    }

    public Predicate<String> getFilter() {
        return filter;
    }

    public void putPayload(Object payload) {
        if (payload != null) {
            payloadMap.put(payload.getClass().getName(), payload);
        }
    }

    public void putPayload(String name, Object payload) {
        payloadMap.put(name, payload);
    }

    public Map<String,Object> getPayloadMap() {
        return Collections.unmodifiableMap(payloadMap);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayload(Class<T> payloadClazz) {
        return (T) payloadMap.get(payloadClazz.getName());
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayload(String payloadName) {
        return (T) payloadMap.get(payloadName);
    }

    /** @return a new Stats normalized to the given unit. */
    public Stats as(Unit<?> unit) {
        return new Stats(type, map, unit, payloadMap, null);
    }

    /** @return a new Stats with the merged measures of the two Stats. */
    public Stats join(Stats other) {
        if (!type.equals(other.type)) {
            throw new RuntimeException("mismatching types: " +
                    "this: " + type.toString() +
                    " != other: " + other.type.toString());
        }
        Map<PathName,DimensionalMeasure> m = new IndexedHashMap<>();
        m.putAll(getMeasureMap());
        m.putAll(other.getMeasureMap());
        return new Stats(other, m);
    }

    @Override
    public StatsType getStatsType() {
        return type;
    }

    public Unit<?> getUnit() {
        return unit;
    }

    private IndexedHashMap<PathName, DimensionalMeasure> createNormalizedMap(
            Map<PathName,DimensionalMeasure> measures, Unit<?> unit) {
        IndexedHashMap<PathName,DimensionalMeasure> m =
                new IndexedHashMap<>(measures.size());
        measures.forEach((PathName n, DimensionalMeasure d) -> {
            m.put(n, d.in(unit));
        });
        return m.unmodifiableView();
    }

    public Map<PathName, DimensionalMeasure> getMeasureMap() {
        return map;
    }

    @Override
    public DimensionalMeasure getFirstMeasure() {
        return map.values().iterator().next();
    }

    @Override
    public DimensionalMeasure getMeasure(CharSequence testName)
            throws IllegalStateException {
        PathName pname = PN.pname(testName);
        DimensionalMeasure m = map.get(pname);
        if (m == null) {
            throw new MeasureNotFoundException(testName, map.keySet());
        }
        return m;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public List<PathName> getNames() {
        return map.keyList();
    }

    public MeasureRatio getRatio(CharSequence testName1, CharSequence testName2,
            Ratio confidence) {
        Measure one = getMeasure(testName1);
        Measure two = getMeasure(testName2);
        return new MeasureRatio(one, two, confidence);
    }

    // see MeasureRatioCalculator
    public MeasureRatio getRatioWithRef(CharSequence testName, Ratio confidence) {
        Measure m = getMeasure(testName);
        Measure ref = getMeasure(getReferenceMeasureName());
        return new MeasureRatio(m, ref, confidence);
    }

    /**
     * Calculates a probability that the pair of means are significantly
     * different from each other (actually using Games-Howell algorithm).
     *
     * @param testName1 name of the first test
     * @param testName2 name of the second test
     * @return the Games-Howell's Honest Significant Difference
     */
    public double getSignificance(CharSequence testName1, CharSequence testName2) {
        int idx1 = map.getIndexOfKey(PN.pname(testName1));
        int idx2 = map.getIndexOfKey(PN.pname(testName2));
        return multiMeasure.gamesHowellPValue(idx1, idx2);
    }

    /**
     * Calculates a probability that given mean is significantly
     * different from the one of the slower test
     * (actually using Games-Howell algorithm).
     */
    public double getSignificanceComparedToRef(CharSequence testName) {
        int idx1 = map.getIndexOfKey(PN.pname(testName));
        if (idx1 == -1) {
            return -1.0;
        }
        return multiMeasure.gamesHowellPValue(idx1, refMeasure.getIndex());
    }

    /**
     * @return the higher margin of error of the ratios of each measure
     *         in the experiment confronted with the bigger one. It's an
     *         estimation of the accuracy of the experiment.
     */
    public Ratio getMaximumPercentageMargin(Ratio confidence) {
        double max = 0;
        for (CharSequence name : getNames()) {
            double moe = getRatioWithRef(name, confidence).getMarginOfError();
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
     * Finds the minimum value of the Games-Howell HSD over all pairs
     * of experiments.
     * It's an estimation of the statistical significance of the collected data.
     * The Statistical Significance Pair Test can only be performed if ANOVA is
     * either close to 0 or to 1.
     *
     * @return the minimum value of the Statistical Significance Games-Howell
     *          test applied to all test pairs.
     */
    public double getMinStatisticalPairSignificance() {
        double min = Double.POSITIVE_INFINITY;
        int size = multiMeasure.getMeasureCount();
        for (int i=0; i<size; i++) {
            for (int j=0; j<=i; j++) {
                double significanceProb = multiMeasure.gamesHowellPValue(i, j);
                if (significanceProb < min) {
                    min = significanceProb;
                }
            }
        }
        return min;
    }

    public CharSequence getReferenceMeasureName() {
        return refMeasure.getName();
    }

    private static Unit<?> getArmonizedUnit(
            Collection<DimensionalMeasure> measures) {
        QuantityList.Builder builder = QuantityList.builder();
        measures.forEach( (DimensionalMeasure dm) ->
            builder.add(dm.getMean(), dm.getUnit()) );
        return builder.build().getUnit();
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
        final Stats other = (Stats) obj;
        if (!Objects.equals(this.map, other.map)) {
            return false;
        }
        return true;
    }

    @Override
    public Stats appendTo(Appendable appendable) {
        try {
            StatsTableStringGenerator.INSTANCE.appendTo(appendable, this);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public Printable<?> getPrintableTukeyMatrix(
            Ratio confidence, boolean onlyEqualTest) {
        if (significancePrintable == null) {
            significancePrintable =
                    new SignificancePrintable(this,confidence, onlyEqualTest);
        }
        return significancePrintable;
    }

    private static class SignificancePrintable
            extends Printable<SignificancePrintable> {
        private final SignificanceMatrixStringGenerator significanceStringGen;
        private final Stats stats;

        public SignificancePrintable(Stats stats,
                Ratio confidence, boolean onlyEqualTest) {
            this.significanceStringGen =
                    new SignificanceMatrixStringGenerator(confidence, onlyEqualTest);
            this.stats = stats;
        }

        @Override
        public SignificancePrintable appendTo(Appendable appendable) {
            try {
                significanceStringGen.appendTo(appendable, stats);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return this;
        }
    }
}
