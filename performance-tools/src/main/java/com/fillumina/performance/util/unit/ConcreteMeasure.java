package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ConcreteMeasure extends Measure {

    Unit getUnit();
}
