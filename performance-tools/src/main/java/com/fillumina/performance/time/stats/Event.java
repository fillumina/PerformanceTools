package com.fillumina.performance.time.stats;

import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.DefaultDimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalMeasure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Event {
    private final String name;
    private final OnlineMeasure measure = new OnlineMeasure();
    private long lastAccess;

    public Event(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    DimensionalMeasure getMeasure() {
        return new DefaultDimensionalMeasure(measure ,AverageTimeUnit.NANOSECONDS);
    }

    void reset() {
        measure.clear();
        lastAccess = System.nanoTime();
    }

    /** @return always true so to be used with '<tt>assert event.fire();</tt>'. */
    public boolean fire() {
        long elapsed = System.nanoTime() - lastAccess;
        measure.addSample(elapsed);
        lastAccess = System.nanoTime();
        return true;
    }

    @Override
    public String toString() {
        return name + " " + measure.toString();
    }
}
