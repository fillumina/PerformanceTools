package com.fillumina.performance.util;

import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Looper {

    public static int loop(int cycles, Runnable runnable) {
        for (int i=0,l=cycles; i<l; i++) {
            runnable.run();
        }
        return cycles;
    }

    public static int loop(Quantity<IntervalUnit> time, Runnable runnable) {
        int counter = 0;
        long intervalNs = (long) time.as(IntervalUnit.NANOSECONDS);
        long endTime = System.nanoTime() + intervalNs;
        do {
            counter++;
            runnable.run();
        } while (System.nanoTime() < endTime);
        return counter;
    }
}
