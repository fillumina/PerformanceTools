package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;

/**
 * Test with stable CPU performances, zero memory allocated and
 * about 16 bytes used.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrTestable extends Testable {
    private final LinearFeedbackShiftRegister lfsr =
            new LinearFeedbackShiftRegister();

    @Override
    public void test() {
        Sink.drain(lfsr.next());
    }
}
