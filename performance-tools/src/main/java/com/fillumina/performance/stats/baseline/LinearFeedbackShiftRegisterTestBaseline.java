package com.fillumina.performance.stats.baseline;

import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinearFeedbackShiftRegisterTestBaseline extends AbstractTestable
        implements TestableBaseline {

    public static final LinearFeedbackShiftRegisterTestBaseline INSTANCE =
            new LinearFeedbackShiftRegisterTestBaseline();

    private final LinearFeedbackShiftRegister lfsr =
            new LinearFeedbackShiftRegister();

    protected LinearFeedbackShiftRegisterTestBaseline() {}

    @Override
    public Object test() {
        return lfsr.next();
    }

    @Override
    public int getNanoseconds() {
        return 0;
    }
}