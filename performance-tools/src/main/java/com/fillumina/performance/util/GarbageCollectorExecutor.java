package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class GarbageCollectorExecutor {

    /**
     * Performs a {@link System#gc()} and wait the given number of
     * milliseconds (this usually helps the JVM to choose to effectively perform
     * garbage collection which by specifications is optional).
     *
     * @param millis number of milliseconds to wait for the GC to take place
     *        use negative number or 0 to not execute gc.
     */
    public static void performGarbageCollection(int millis) {
        if (millis > 0) {
            System.gc();
            try {
                // helps the JVM to actually scedule a GC
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
