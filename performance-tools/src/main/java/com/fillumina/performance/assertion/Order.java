package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class Order {

    private final double confidence;

    public Order(Ratio tolerance) {
        this.confidence = 1 + tolerance.getValue();
    }

    public boolean lt(double a, double b) {
        return (a / b) < confidence;
    }

    public boolean gt(double a, double b) {
        return (b / a) < confidence;
    }
}
