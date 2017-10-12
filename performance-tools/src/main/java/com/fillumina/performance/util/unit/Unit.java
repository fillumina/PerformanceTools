package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Unit<U extends Unit<U>> {

    Units<U> units();

    /** Multiplication factor of current unit as respect to base. */
    double getFactor();

    @SuppressWarnings("unchecked")
    default Quantity<U> quantity(double value) {
        return new Quantity<>(value, (U)this);
    }

    /**
     * Converts a value expressed as the given units into the
     * current unit of measure.
     */
    default double convert(double value, U unit) {
        return value / unit.convertFromBase(1.0) / getFactor();
    }

    /** Converts from the base unit. */
    default double convertFromBase(final double value) {
        return value / getFactor();
    }

    /** Converts to the base unit. */
    default double convertToBase(final double value) {
        return value * getFactor();
    }

    default String getName() {
        return getClass().getSimpleName().replace("Unit", "");
    }
}
