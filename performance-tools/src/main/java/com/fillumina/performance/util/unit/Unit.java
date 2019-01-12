package com.fillumina.performance.util.unit;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Represents the unit of a measure.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Unit<U extends Unit<U>> extends Comparable<U> {

    Units<U> units();

    /** Multiplication factor of current unit in respect to base. */
    double getFactor();

    int ordinal();
    String name();

    default List<U> valueList() {
        return units().valueList();
    }

    default U getBase() {
        return units().getBase();
    }

    @SuppressWarnings("unchecked")
    default Quantity<U> quantity(double value) {
        return new Quantity<>(value, (U)this);
    }

    @SuppressWarnings("unchecked")
    default Quantity<U> zero() {
        return new Quantity<>(0, (U)this);
    }

    default double getConversionFactorTo(Unit<?> unit) {
        assertSameTypeWith(unit);
        return getFactor() / unit.getFactor();
    }

    /**
     * Converts a value expressed in the given unit into the
     * current unit of measure.
     */
    default double convert(double value, Unit<?> unit) {
        assertSameTypeWith(unit);
        return value * unit.getConversionFactorTo(this);
    }

    /** Converts from the base unit (don't make assumptions about base). */
    default double convertFromBase(final double value) {
        return value / getFactor();
    }

    /** Converts to the base unit (don't make assumptions about base). */
    default double convertToBase(final double value) {
        return value * getFactor();
    }

    default void assertSameTypeWith(Unit<?> other)
            throws MismatchedUnitRuntimeException {
        if (!isSameType(other)) {
            throw new MismatchedUnitRuntimeException(this, other);
        }
    }

    default boolean isSameType(Unit<?> other) {
        return getBase() == other.getBase();
    }

    default String getUnitName() {
        return getClass().getSimpleName().replace("Unit", "");
    }

    default U bestUnit(double... array) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double v : array) {
            if (v < min) {
                min = v;
            }
            if (v > max) {
                max = v;
            }
        }
        return Unit.this.bestUnit(min, max);
    }

    default U bestUnit(Collection<Double> coll) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double v : coll) {
            if (v < min) {
                min = v;
            }
            if (v > max) {
                max = v;
            }
        }
        return Unit.this.bestUnit(min, max);
    }

    default U bestUnit(double min, double max) {
        U minUnit = Unit.this.bestUnit(min);
        int minIndex = minUnit.ordinal();
        U maxUnit = Unit.this.bestUnit(max);
        int maxIndex = maxUnit.ordinal();

        int avgUnitIndex =
                (int) Math.floor(minIndex + 1.0 * (maxIndex - minIndex) / 2.0);
        List<U> valueList = valueList();
        U r = valueList.get(avgUnitIndex);
        while (avgUnitIndex > 0 && r.convertFromBase(min) < 0.1) {
            avgUnitIndex--;
            r = valueList.get(avgUnitIndex);
        }

        return valueList.get(avgUnitIndex);
    }

    /**
     * @param value expressed in base unit (the one with factor = 1.0)
     * @return the most closed {@link Unit} scale.
     */
    @SuppressWarnings("unchecked")
    default U bestUnit(double value) {
        List<U> list = valueList();
        if (list.size() == 1) {
            return list.get(0);
        }
        U unit = null;
        for (int i=1,l=list.size(); i<l; i++) {
            unit = list.get(i);
            double c = Math.abs(unit.convert(value, (U)this));
            if (c < 1) {
                return unit.prev();
            }
        }
        return unit;
    }

    default U next() {
        int index = ordinal();
        if (index < valueList().size() - 1) {
            return valueList().get(index + 1);
        }
        return null;
    }

    default U prev() {
        int index = ordinal();
        if (index > 0) {
            return valueList().get(index - 1);
        }
        return null;
    }

    default String toString(double value) {
        return toString(value, 4);
    }

    default String toString(double value, int precision) {
        return String.format(Locale.US, "%,." + precision + "f %s", value, this);
    }

    default String toBestString(double value) {
        return toBestString(value, 2);
    }

    default String toBestString(double value, int precision) {
        Unit<?> bestUnit = bestUnit(value);
        @SuppressWarnings("unchecked")
        double converted = bestUnit.convert(value, (U)this);
        return String.format(Locale.US, "%,." + precision + "f %s",
                converted, bestUnit);
    }

    default String toPrettyString(double value) {
        return toPrettyString(value, 20);
    }

    default String toPrettyString(double value, int groups) {
        double baseValue = convertToBase(value);
        @SuppressWarnings("unchecked")
        U unit = Unit.this.bestUnit(value);
        StringBuilder buf = new StringBuilder();
        for (int i=groups; i>0; i--) {
            double converted = unit.convertFromBase(baseValue);
            double remain = Math.floor(converted);
            if (remain > 0) {
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                buf.append(String.format(Locale.US, "%,.0f %s", remain, unit));
            }
            baseValue -= unit.convertToBase(remain);
            unit = unit.prev();
            if (unit == null) {
                break;
            }
        }
        return buf.toString();
    }

}
