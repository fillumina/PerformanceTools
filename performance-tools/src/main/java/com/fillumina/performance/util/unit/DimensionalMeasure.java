package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface DimensionalMeasure extends Measure {

    Unit getUnit();

    String toString(Unit unit);

    String toStringForConfidence(Ratio confidence, Unit unit);
}
