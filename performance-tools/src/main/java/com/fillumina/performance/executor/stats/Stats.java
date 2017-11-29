package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.Printable;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.Magnitude;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
public class Stats extends Printable<Stats>
        implements StatsTyped, Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    public interface Type { Type DEFAULT = new Type() {}; }

    private final Type type;
    private final BiggerMeasure refMeasure;
    private final List<TName> names;
    private final Map<TName, DimensionalMeasure> map;
    private final MultiMeasureSignificance multiMeasure;

    private TukeyPrintable tukeyPrintable;

    /** Copy constructor. */
    public Stats(Stats other) {
        this(other.type, other.map);
    }

    public static Stats create(
            Map<? extends CharSequence,? extends Measure> measures) {
        Map<TName,DimensionalMeasure> map = new LinkedHashMap<>();
        measures.forEach((CharSequence s, Measure m) ->
                map.put(TN.tname(s),
                        new DimensionalOnlineMeasure(Magnitude.UNIT, m)) );
        return new Stats(Type.DEFAULT, map);
    }

    public Stats(Type type, Map<TName,DimensionalMeasure> measures) {
        this.type = type;
        this.map = Collections.unmodifiableMap(new LinkedHashMap<>(measures));
        this.names = Collections.unmodifiableList(new ArrayList<>(measures.keySet()));
        this.refMeasure = new BiggerMeasure(measures);
        this.multiMeasure = new MultiMeasureSignificance(measures.values());
    }

    public Stats join(Stats other) {
        if (!type.equals(other.type)) {
            throw new RuntimeException("mismatching types: " +
                    "this: " + type.toString() +
                    " != other: " + other.type.toString());
        }
        Map<TName,DimensionalMeasure> m = new LinkedHashMap<>();
        m.putAll(getMeasureMap());
        m.putAll(other.getMeasureMap());
        return new Stats(other.type , m);
    }

    public Map<TName, DimensionalMeasure> getMeasureMap() {
        return map;
    }

    @Override
    public Type getStatsType() {
        return type;
    }

    @Override
    public Measure getMeasure(CharSequence testName)
            throws IllegalStateException {
        TName tname = TN.tname(testName);
        Measure single = map.get(tname);
        if (single == null) {
            throw new TestNotFoundException(testName, map.keySet());
        }
        return single;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public List<TName> getNames() {
        return names;
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
     * Calculates an estimation that the pair of means are significantly
     * different from each other.
     *
     * @param testName1 name of the first test
     * @param testName2 name of the second test
     * @return the Tukey's Honest Significant Difference
     */
    public double getTukeyHsd(CharSequence testName1, CharSequence testName2) {
        int idx1 = names.indexOf(TN.tname(testName1));
        int idx2 = names.indexOf(TN.tname(testName2));
        return multiMeasure.tukeyKramerHsdPValue(idx1, idx2);
    }

    public double getTukeyHsdComparedToRef(CharSequence testName) {
        int idx1 = names.indexOf(TN.tname(testName));
        if (idx1 == -1) {
            return -1.0;
        }
        return multiMeasure.tukeyKramerHsdPValue(idx1, refMeasure.getIndex());
    }

    /**
     * @return the higher margin of error of the ratios of each measure
     *         in the experiment confronted with the slower one. It's an
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

    public CharSequence getReferenceMeasureName() {
        return refMeasure.getName();
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

    public Printable<?> getPrintableTukeyMatrix() {
        if (tukeyPrintable == null) {
            tukeyPrintable = new TukeyPrintable(this);
        }
        return tukeyPrintable;
    }

    private static class TukeyPrintable extends Printable<TukeyPrintable> {
        private final Stats stats;

        public TukeyPrintable(Stats stats) {
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
