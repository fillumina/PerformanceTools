package com.fillumina.performance.util.stats;

import static java.lang.Math.log;
import java.util.Collection;

/**
 * Given the points the class calculates the power function that fits:
 * y = A x<sup>B</sup>
 *
 * @see http://mathworld.wolfram.com/LeastSquaresFittingPowerLaw.html
 * @see http://mathworld.wolfram.com/LeastSquaresFitting.html
 * @see https://math.stackexchange.com/questions/3625/easy-to-implement-method-to-fit-a-power-function-regression
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PowerFitting implements FittingCurve {

    private final double a, b;

    public PowerFitting(Collection<? extends Point> list) {
        final double n = list.size();
        double sumLogXLogY = 0, sumLogX = 0, sumLogY = 0, sumLogXPow2 = 0;
        for (Point p : list) {
            double lx = log(p.getX());
            double ly = log(p.getY());

            sumLogXLogY += lx * ly;
            sumLogX += lx;
            sumLogY += ly;
            sumLogXPow2 += lx * lx;
        }

        this.b = (n * sumLogXLogY - sumLogX * sumLogY) /
                (n * sumLogXPow2 - sumLogX * sumLogX);

        this.a = Math.exp((sumLogY - b * sumLogX) / n);
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    @Override
    public double calculateXGivingY(double y) {
        return Math.pow(y/a, 1/b);
    }

    @Override
    public double calculateYGivingX(double x) {
        return a * Math.pow(x, b);
    }

    @Override
    public String toString() {
        return "y = " + getA() + " * x ^ " + getB();
    }
}
