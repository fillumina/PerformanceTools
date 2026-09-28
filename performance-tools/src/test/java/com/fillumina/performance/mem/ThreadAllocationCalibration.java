package com.fillumina.performance.mem;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;

/** Standalone repeated-JVM probe; not an assertion about all CI hosts. */
public final class ThreadAllocationCalibration {
    private static volatile Object escape;

    private ThreadAllocationCalibration() {}

    public static void main(String[] args) {
        if (!ThreadAllocationComparison.isSupported()) {
            System.out.println("UNSUPPORTED");
            return;
        }
        int equalFalseGates = 0;
        int unequalMisses = 0;
        int smallIncreaseMisses = 0;
        int invalid = 0;
        for (int i = 0; i < 20; i++) {
            Stats equal = ThreadAllocationComparison.compare(
                    () -> allocate(), () -> allocate(), 33);
            MeasureRatio same = equal.getRatio("candidate", "reference", Ratio.P_99);
            if (!same.isValid()) {
                invalid++;
            } else if (same.getLowerBound() > 1 || same.getUpperBound() < 1) {
                equalFalseGates++;
            }
            Stats unequal = ThreadAllocationComparison.compare(
                    () -> allocate(), () -> { allocate(); allocate(); }, 33);
            MeasureRatio different = unequal.getRatio("candidate", "reference", Ratio.P_99);
            if (!different.isValid()) {
                invalid++;
            } else if (different.getLowerBound() <= 1) {
                unequalMisses++;
            }
            Stats smallIncrease = ThreadAllocationComparison.compare(
                    () -> allocateMany(16), () -> allocateMany(17), 33);
            MeasureRatio small = smallIncrease.getRatio("candidate", "reference", Ratio.P_99);
            if (!small.isValid()) {
                invalid++;
            } else if (small.getLowerBound() <= 1) {
                smallIncreaseMisses++;
            }
        }
        if (escape != null) {
            throw new AssertionError("workload retained an array");
        }
        System.out.println("equal_false_gates=" + equalFalseGates +
                " unequal_misses=" + unequalMisses +
                " small_increase_misses=" + smallIncreaseMisses + " invalid=" + invalid);
    }

    private static void allocateMany(int count) {
        for (int i = 0; i < count; i++) {
            allocate();
        }
    }

    private static void allocate() {
        escape = new byte[256];
        escape = null;
    }
}
