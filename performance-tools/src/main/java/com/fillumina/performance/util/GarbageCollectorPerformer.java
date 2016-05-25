package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class GarbageCollectorPerformer {

    public static void gc() {
        System.gc();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
