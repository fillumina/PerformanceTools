package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Unit {

    Units<?> units();

    /** Multiplication factor of current unit in respect to base. */
    double getFactor();

    /**
     * Converts a value expressed in the given units into the
     * current unit of measure.
     */
    default double convert(double value, Unit dimension) {
        return value / dimension.convertFromBase(1.0) / getFactor();
    }

    /** Converts from the minimum factor available. */
    default double convertFromBase(final double value) {
        return value / getFactor();
    }

    /** Converts to the minimum factor available. */
    default double convertToBase(final double value) {
        return value * getFactor();
    }

}
