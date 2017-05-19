package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.rnd.Lfsr;

/**
 * Minimal CPU usage test that doesn't use system calls,
 * has a very small footprint, doesn't allocate any extra memory
 * and it's stable. It is also written in a way that should not be
 * evicted by JVM.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrRunnable implements Runnable {
    private final Lfsr lfsr = new Lfsr();

    @Override
    public void run() {
        if (lfsr.next() == 0) {
            // lfsr is never 0, but JVM doesn't know...
            throw new RuntimeException();
        }
    }
}
