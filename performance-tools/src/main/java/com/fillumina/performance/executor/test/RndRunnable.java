package com.fillumina.performance.executor.test;

import com.fillumina.performance.util.rnd.XorShiftPlusRandom;

/**
 * Minimal CPU usage test that doesn't use system calls,
 * has a very small footprint, doesn't allocate any extra memory
 * and it's quite stable.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RndRunnable implements Runnable {
    private final XorShiftPlusRandom rnd = new XorShiftPlusRandom();

    @Override
    public void run() {
        Sink.drain(rnd.nextInt());
    }
}
