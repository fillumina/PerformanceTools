package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AssertableMultiTest;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NestedStatsVisitor<A extends AssertableMultiTest> {

    public void visit(ComposedName name, A element) {

    }

    void visit(A assertable) {

    }

    @SuppressWarnings("unchecked")
    void visit(Map<ComposedName,?> map) {
        for (Map.Entry<ComposedName, ?> entry : map.entrySet()) {
            ComposedName name = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                visit((Map<ComposedName,?>) value);
            } else {
                visit(name, (A)value);
            }
        }
    }
}
