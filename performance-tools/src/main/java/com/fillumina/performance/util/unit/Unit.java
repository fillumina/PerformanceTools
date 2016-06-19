package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Unit {

    double convert(final double value);

    Unit[] allValues();

    UnitFormatter<? extends Unit> getFormatter();
}
