package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.rnd.Lfsr;

/**
 * Minimal CPU usage test that doesn't use system calls,
 * has a very small footprint, doesn't allocate any extra memory
 * and it's quite stable.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrTestable extends Testable {
    private Lfsr lfsr = new Lfsr();

    @Override
    public void test() {
        Sink.drain(lfsr.next());
    }
}
