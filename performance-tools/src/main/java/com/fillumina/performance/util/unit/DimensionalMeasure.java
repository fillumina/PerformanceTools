package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface DimensionalMeasure extends Measure {

    Unit getUnit();

    String toString(Unit unit);

    String toStringForConfidence(double confidence, Unit unit);
}
