package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.AbstractTestable;
import com.fillumina.performance.infrastructure.Drain;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;

/**
 * Test with stable CPU performances, zero memory allocated and
 * about 16 bytes used.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrTestable extends AbstractTestable {
    private final LinearFeedbackShiftRegister lfsr =
            new LinearFeedbackShiftRegister();

    @Override
    public void test() {
        Drain.drain(lfsr.next());
    }
}
