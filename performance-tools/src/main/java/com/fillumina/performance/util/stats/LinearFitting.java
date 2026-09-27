package com.fillumina.performance.util.stats;

import java.util.Collection;

/**
 * Given the points this class calculates the linear function that fits:
 * {@literal y = m x + b}
 *
 * @see https://en.wikipedia.org/wiki/Simple_linear_regression
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinearFitting implements FittingCurve {

    private final double m, b;

    public LinearFitting(Collection<? extends Point> list) {
        final double n = list.size();
        double sumY = 0, sumX = 0;
        for (Point p : list) {
            double x = p.getX();
            double y = p.getY();

            sumX += x;
            sumY += y;
        }
        double X = sumX / n;
        double Y = sumY / n;

        double sumMNum = 0, sumMDen = 0;
        for (Point p : list) {
            double x = p.getX();
            double y = p.getY();

            sumMNum += (x - X) * (y - Y);
            double s = x - X;
            sumMDen += s * s;
        }

        m = sumMNum / sumMDen;
        b = Y - m * X;
    }

    public double getM() {
        return m;
    }

    public double getB() {
        return b;
    }

    @Override
    public double calculateXGivingY(double y) {
        return (y - b) / m;
    }

    @Override
    public double calculateYGivingX(double x) {
        return m * x + b;
    }

    @Override
    public String toString() {
        return "y = " + getM() + " x + " + getB();
    }
}
