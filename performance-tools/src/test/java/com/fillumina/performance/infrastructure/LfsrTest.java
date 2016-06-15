package com.fillumina.performance.infrastructure;

import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import java.util.Collections;
import java.util.Map;

/**
 * Test that should have stable performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrTest extends AbstractTestable {
    public static final LfsrTest INSTANCE = new LfsrTest();
    public static final Map<String,Testable> SINGLETON_MAP_INSTANCE =
                Collections.<String, Testable>singletonMap(null, INSTANCE);

    private final LinearFeedbackShiftRegister lfsr =
            new LinearFeedbackShiftRegister();

    @Override
    public Object test() {
        return lfsr.next();
    }
}
