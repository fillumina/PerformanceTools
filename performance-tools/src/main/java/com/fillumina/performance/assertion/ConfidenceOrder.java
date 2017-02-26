package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class ConfidenceOrder {

    private final double confidence;

    public ConfidenceOrder(Ratio tolerance) {
        this.confidence = (100.0 + tolerance.getPercentage()) / 100.0;
    }

    public boolean lt(double a, double b) {
        return (a / b) <= confidence;
    }

    public boolean gt(double a, double b) {
        return (b / a) <= confidence;
    }
}
