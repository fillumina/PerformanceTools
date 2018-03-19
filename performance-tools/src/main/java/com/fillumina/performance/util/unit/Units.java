package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.collection.UnmodifiableList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Units<U extends Unit<U>> {

    /** All values in ascending order of factor. */
    private final List<U> valueList;

    /** The unit with factor = 1.0 */
    private final U base;

    /** @param units must be in ascending order. */
    public Units(U... units) {
        U baseUnit = null;
        double prev = Double.NEGATIVE_INFINITY;
        for (U u : units) {
            final double factor = u.getFactor();
            if (factor == 0) {
                throw new RuntimeException("factor cannot be 0");
            }
            if (factor <= prev) {
                throw new RuntimeException(
                        "wrong unit order (must be ordered by factor)");
            }
            if (factor == 1.0) {
                baseUnit = u;
            }
            prev = factor;
        }
        if (baseUnit == null) {
            throw new RuntimeException("no unit with factor = 1 found");
        }
        this.base = baseUnit;
        this.valueList = new UnmodifiableList<>(units);
    }

    public List<U> valueList() {
        return valueList;
    }

    public U getBase() {
        return base;
    }

    public static Unit<?> min(Unit<?> a, Unit<?> b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        a.assertSameTypeWith(b);
        return a.ordinal() < b.ordinal() ? a : b;
    }

    public static Unit<?> max(Unit<?> a, Unit<?> b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        a.assertSameTypeWith(b);
        return a.ordinal() > b.ordinal() ? a : b;
    }
}
