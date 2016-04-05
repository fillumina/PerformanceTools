package com.fillumina.performance.stats;

import java.util.AbstractList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunningMultipleMeasure {

    private final RunningMeasure global;
    private final RunningMeasure[] measures;
    private final List<Measure> unmodifiableList;

    public RunningMultipleMeasure(int numberOfMeasures) {
        this.global = new RunningMeasure();
        measures = new RunningMeasure[numberOfMeasures];
        for (int i=0; i<numberOfMeasures; i++) {
            measures[i] = new RunningMeasure();
        }
        unmodifiableList = new AbstractList<Measure>() {
            @Override
            public Measure get(int index) {
                return measures[index];
            }

            @Override
            public int size() {
                return measures.length;
            }
        };
    }

    public RunningMultipleMeasure add(int measureIndex, double value) {
        measures[measureIndex].add(value);
        global.add(value);
        return this;
    }

    public RunningMultipleMeasure add(Double... values) {
        Double v;
        for (int i=0, len=measures.length; i<len; i++) {
            v = values[i];
            if (v != null) {
                measures[i].add(v);
                global.add(v);
            }
        }
        return this;
    }

    public Measure getGlobal() {
        return global;
    }

    public List<Measure> getMeasures() {
        return unmodifiableList;
    }

    public MultipleMeasure getMultipleMeasure() {
        return new MultipleMeasure(global, measures);
    }
}
