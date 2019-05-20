package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.SingleMeasure;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class DimensionalMeasure extends Measure {

    public abstract Unit<?> getUnit();

    public static DimensionalMeasure of(double ... values) {
        return of(Absolute.UNIT, values);
    }

    public static DimensionalMeasure of(Unit<?> unit, double ... values) {
        if (values.length == 1) {
            return new ImmutableDimensionalMeasure(new SingleMeasure(values[0]), unit);
        }
        return new OnlineDimensionalMeasure(unit, values).toImmutable();
    }

    public static Unit<?> bestUnit(DimensionalMeasure ... measures) {
        final int length = measures.length;
        double[] array = new double[length];
        Unit<?> base = measures[0].getUnit().getBase();
        for (int i=0; i<length; i++) {
            DimensionalMeasure m = measures[i];
            final Unit<?> u = m.getUnit();
            array[i] = u.convertToBase(m.getMean());
        }
        return base.bestUnit(array);
    }

    public static List<DimensionalMeasure> armonize(
            Iterable<DimensionalMeasure> iterable) {
        List<Double> values = new ArrayList<>();
        Unit<?> u = null;
        for (DimensionalMeasure dm : iterable) {
            u = dm.getUnit();
            values.add(u.convertToBase(dm.getMean()));
        }
        Unit<?> bestUnit = u.bestUnit(values);
        List<DimensionalMeasure> list = new ArrayList<>(values.size());
        for (DimensionalMeasure dm : iterable) {
            list.add(dm.in(bestUnit));
        }
        return list;
    }

    @Override
    public MeasureRatio ratio(Measure m, Ratio confidence) {
        return new MeasureRatio(this, convertToSameUnit(m), confidence);
    }

    @Override
    public DimensionalMeasure join(Measure m) {
        return new ImmutableDimensionalMeasure(
                super.join(convertToSameUnit(m)), getUnit());
    }

    @Override
    public DimensionalMeasure multiplyBy(double value) {
        return new ImmutableDimensionalMeasure(super.multiplyBy(value), getUnit());
    }

    @Override
    public DimensionalMeasure divideBy(double value) {
        return new ImmutableDimensionalMeasure(super.divideBy(value), getUnit());
    }

    @Override
    public DimensionalMeasure subtract(Measure m) {
        return new ImmutableDimensionalMeasure(
                super.subtract(convertToSameUnit(m)), getUnit());
    }

    @Override
    public DimensionalMeasure sum(Measure m) {
        return new ImmutableDimensionalMeasure(
                super.sum(convertToSameUnit(m)), getUnit());
    }

    /** @return an immutable snapshot of the current measure. */
    @Override
    public DimensionalMeasure toImmutable() {
        return new ImmutableDimensionalMeasure(this);
    }

    protected Measure convertToSameUnit(Measure m) {
        if (m instanceof DimensionalMeasure) {
            return ((DimensionalMeasure) m).in(getUnit());
        }
        return m;
    }

    public String toString(Unit<?> unit) {
        return toStringForConfidence(Ratio.P_95, unit);
    }

    /**
     * @param unit the unit to be converted into.
     *          <i>the given unit must be of the same type of the actual one!</i>
     *
     * @return a new {@link DimensionalMeasure} converted to the given
     *           {@link Unit}.
     */
    public DimensionalMeasure in(Unit<?> unit) {
        if (getUnit() == unit) {
            return this;
        }
        getUnit().assertSameTypeWith(unit);
        return new ImmutableDimensionalMeasure(
                multiplyBy(getUnit().getConversionFactorTo(unit)), unit);
    }

    public String toStringForConfidenceWitoutSamples(Ratio confidence,
            Unit<?> unit) {
        if (!getUnit().isSameType(unit)) {
            throw new RuntimeException("type mismatch: " +
                    getUnit().toString() + " vs " + unit.toString());
        }
        double mean = unit.convert(getMean(), getUnit());
        double moe = unit.convert(getMarginOfError(confidence), getUnit());
        if (Double.isNaN(moe)) {
            return String.format(Locale.US, "%.3f %s", mean, unit);
        }
        return String.format(Locale.US, "%.3f +/- %.3f %s",
            mean, moe, unit);
    }

    public String toStringForConfidence(Ratio confidence, Unit<?> unit) {
        getUnit().assertSameTypeWith(unit);
        double mean = unit.convert(getMean(), getUnit());
        double moe = unit.convert(getMarginOfError(confidence), getUnit());
        return String.format(Locale.US, "%.3f +/- %.3f (%d samples) %s",
            mean, moe, getCount(), unit);
    }
}
