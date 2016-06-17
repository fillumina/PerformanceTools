package com.fillumina.performance.infrastructure;

import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati
 */
public class NullTest extends AbstractTestable {
    public static final NullTest INSTANCE = new NullTest();
    public static final Map<String,Testable> SINGLETON_MAP_INSTANCE =
                Collections.<String, Testable>singletonMap(null, INSTANCE);

    private NullTest() {}

    @Override
    public Object test() {
        return null;
    }
}
