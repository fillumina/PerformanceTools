package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.rnd.Lfsr;

/**
 * Minimal CPU usage run that doesn't use system calls,
 has a very small footprint, doesn't allocate any extra memory
 and it's quite stable.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrTestable implements Runnable {
    private final Lfsr lfsr = new Lfsr();

    @Override
    public void run() {
        if (lfsr.next() == 0) {
            // lfsr is never 0, but JVM doesn't know...
            throw new RuntimeException();
        }
    }
}
