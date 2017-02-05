package com.fillumina.performance.infrastructure;

import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;

/**
 * Test that should have stable performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrTest extends AbstractTestable {
    private final LinearFeedbackShiftRegister lfsr =
            new LinearFeedbackShiftRegister();

    @Override
    public Object test() {
        return lfsr.next();
    }
}
