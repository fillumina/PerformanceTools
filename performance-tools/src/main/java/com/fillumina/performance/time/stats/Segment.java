package com.fillumina.performance.time.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.DefaultDimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalMeasure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Segment {
    private final TName name;
    private final Segment previous;
    private final OnlineMeasure measure = new OnlineMeasure();
    private long lastAccess;

    public Segment(String name, Segment previous) {
        this.name = TN.tname(name);
        this.previous = previous;
    }

    public Segment(TName name, Segment previous) {
        this.name = name;
        this.previous = previous;
    }

    TName getName() {
        return name;
    }

    DimensionalMeasure getMeasure() {
        return new DefaultDimensionalMeasure(measure, AverageTimeUnit.NANOSECONDS);
    }

    void reset() {
        measure.clear();
        lastAccess = System.nanoTime();
    }

    /** @return always true so to be used with '<tt>assert event.fire();</tt>'. */
    public boolean fire() {
        long now = System.nanoTime();
        long elapsed = now - previous.getLastAccess();
        measure.addSample(elapsed);
        lastAccess = System.nanoTime();
        return true;
    }

    @Override
    public String toString() {
        return name + " " + measure.toString();
    }

    protected long getLastAccess() {
        return lastAccess;
    }
}
