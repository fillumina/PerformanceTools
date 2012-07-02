package com.fillumina.performance.assertion;

import com.fillumina.performance.AbstractPerformanceConsumer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public class AssertPerformancesSuite
        extends AbstractPerformanceConsumer<AssertPerformancesSuite> {
    private static final long serialVersionUID = 1L;

    private Map<String, AssertPerformance> map = new HashMap<>();

    public AssertPerformance addAssertionForTest(final String name) {
        final AssertPerformance assertPerformance = new AssertPerformance();
        assertPerformance.setMessage(name);
        map.put(name, assertPerformance);
        return assertPerformance;
    }

    @Override
    public void consume() {
        final AssertPerformance assertPerformance = map.get(getMessage());
        if (assertPerformance != null) {
            assertPerformance.consume();
        }
    }
}
