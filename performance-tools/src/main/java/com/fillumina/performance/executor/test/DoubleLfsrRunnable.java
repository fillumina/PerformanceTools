package com.fillumina.performance.executor.test;

import com.fillumina.performance.util.rnd.Lfsr;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DoubleLfsrRunnable implements Runnable {

    private final Lfsr lfsr = new Lfsr();

    @Override
    public void run() {
        if (lfsr.next() == 0) {
            // lfsr is never 0, but JVM doesn't know...
            throw new AssertionError();
        }
        if (lfsr.next() == 0) {
            // lfsr is never 0, but JVM doesn't know...
            throw new AssertionError();
        }
    }

}
