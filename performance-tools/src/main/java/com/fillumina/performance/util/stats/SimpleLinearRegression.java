package com.fillumina.performance.util.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds the simple linear regression of the given points and returns
 * the a and b parameters that defines the line: {@code y = a + bx}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SimpleLinearRegression {

    public static class Point {
        private final double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Builder {
        private final List<Point> list = new ArrayList<>();

        public Builder add(double x, double y) {
            list.add(new Point(x,y));
            return this;
        }

        public SimpleLinearRegression build() {
            return new SimpleLinearRegression(list);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final double a, b, rSquared;

    public SimpleLinearRegression(Iterable<? extends Point> list) {
        int n = 0;
        double sumX = 0;
        double sumY = 0;
        double sumSquaredX = 0;
        double sumSquaredY = 0;
        double sumXY = 0;

        for (Point p : list) {
            n++;
            sumX += p.x;
            sumY += p.y;
            sumSquaredX += p.x * p.x;
            sumSquaredY += p.y * p.y;
            sumXY += p.x * p.y;
        }

        double squaredSumX = sumX * sumX;
        double squaredSumY = sumY * sumY;
        double subX = n * sumSquaredX - squaredSumX;

        this.a = ( sumY * sumSquaredX - sumX * sumXY ) / subX;

        double subXY = n * sumXY - sumX * sumY;

        this.b = subXY / subX;

        this.rSquared = (subXY * subXY) /
                (subX * (n * sumSquaredY - squaredSumY));
    }

    /**
     * It's a percentage {@code 0.0 < r2 < 1.0 } representing the goodness
     * of the linear estimation where 0 is none and 1 is perfect alignment.
     */
    public double getRSquared() {
        return rSquared;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    @Override
    public String toString() {
        return "SimpleLinearRegression{a=" + a +
                ", b=" + b +
                ", r2=" + rSquared + '}';
    }
}
