package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Unit {

    /**
     * Converts a value expressed in the given units into the
     * current unit of measure.
     */
    double convert(double value, Unit dimension);

    /** Converts into the minimum factor available. */
    double convertFromBase(double value);
}
