package com.fillumina.performance.util.stats;

import static java.lang.Math.log;
import static java.lang.Math.pow;
import java.util.function.Function;

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
public class PowerFitting {

    public static interface Point {
        double getX();
        double getY();
    }

    private final Point[] data;     // actual points
    private final double a, b;

    public PowerFitting(Point[] data) {
        this.data = data;
        this.b = calculateB();
        this.a = calculateA(b);
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double calculateXGivingY(double y) {
        return Math.pow(y/a, 1/b);
    }

    public double calculateYGivingX(double x) {
        return a * Math.pow(x, b);
    }

    private double calculateB() {
        final double n = data.length;

        double num = n * sum(p -> log(p.getX()) * log(p.getY())) -
                sum(p -> log(p.getX())) * sum(p -> log(p.getY()));

        double den = n * sum(p -> pow(log(p.getX()), 2)) -
                pow(sum(p -> log(p.getX())), 2);

        return num / den;
    }

    private double calculateA(double b) {
        final double n = data.length;

        double a = (sum(p -> log(p.getY())) - b * sum(p -> log(p.getX()))) / n;

        return Math.exp(a);
    }

    private double sum(Function<Point, Double> fun) {
        double sum = 0;
        for (Point p : data) {

            sum += fun.apply(p);
        }
        return sum;
    }
}
