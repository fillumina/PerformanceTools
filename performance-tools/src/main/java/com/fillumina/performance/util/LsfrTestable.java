package com.fillumina.performance.util;

import com.fillumina.performance.speed.sample.AbstractTestable;

/**
 * Minimal CPU usage test that doesn't use system calls,
 * has a very small footprint, doesn't allocate any extra memory
 * and it's quite stable.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO it's testable, move out of here
public class LsfrTestable extends AbstractTestable {
    private LinearFeedbackShiftRegister lfsr = new LinearFeedbackShiftRegister();

    @Override
    public Object test() {
        return lfsr.next();
    }
}
