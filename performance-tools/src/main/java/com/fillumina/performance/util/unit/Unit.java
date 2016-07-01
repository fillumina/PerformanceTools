package com.fillumina.performance.util.unit;

import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Unit {

    /**
     * Converts a value expressed in the given units into the
     * current unit of measure.
     */
    double convert(double value, Unit unit);

    /** Converts into the minimum factor available. */
    double convertFromBase(double value);

    Unit getBase();

    /** @return all available units of measure. */
    List<Unit> allAvailableUnitOfMeasures();

    UnitFormatter<? extends Unit> getFormatter();
}
