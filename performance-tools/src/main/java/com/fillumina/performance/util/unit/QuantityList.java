package com.fillumina.performance.util.unit;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class QuantityList
        extends AbstractList<Double>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Quantity<?>> list = new ArrayList<>();
        private Unit<?> baseUnit;
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;

        public Builder add(double value, Unit<?> unit) {
            return add( Quantity.from(value, unit) );
        }

        public Builder add(Quantity<?> q) {
            list.add(q);
            Unit<?> unit = q.getUnit();
            double baseValue = unit.convertToBase(q.getValue());
            if (baseUnit == null) {
                baseUnit = unit.getBase();
            } else {
                baseUnit.assertSameTypeWith(unit);
            }
            min = Math.min(min, baseValue);
            max = Math.max(max, baseValue);
            return this;
        }

        public QuantityList build() {
            if (list.isEmpty()) {
                // TODO not sure if it makes any sense
                return QuantityList.EMPTY;
            }
            Unit<?> bestUnit = baseUnit.bestUnit(min, max);
            return new QuantityList(list, bestUnit);
        }
    }

    public static final QuantityList EMPTY = new QuantityList(
            Collections.emptyList(), null);

    private final List<Quantity<?>> values;
    private final Unit<?> unit;

    private QuantityList(List<Quantity<?>> list, Unit<?> unit) {
        this.unit = unit;
        this.values = list;
    }

    public Unit<?> getUnit() {
        return unit;
    }

    public Quantity<?> getQuantity(int index) {
        return values.get(index);
    }

    @Override
    public Double get(int index) {
        return values.get(index).as(unit);
    }

    @Override
    public int size() {
        return values.size();
    }
}
