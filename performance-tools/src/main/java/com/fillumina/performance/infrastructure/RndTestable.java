package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.rnd.XorShiftPlusRandom;

/**
 * Minimal CPU usage run that doesn't use system calls,
 has a very small footprint, doesn't allocate any extra memory
 and it's quite stable.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RndTestable extends Testable {
    private final XorShiftPlusRandom rnd = new XorShiftPlusRandom();

    @Override
    public void run() {
        Sink.drain(rnd.nextInt());
    }
}
