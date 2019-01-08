package com.fillumina.performance.util.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface FittingCurve {

    double calculateXGivingY(double y);

    double calculateYGivingX(double x);

}
