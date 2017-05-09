package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.util.collection.LinkedMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiAssertion {

    private final LinkedMap<String, Assertion<?>> assertionMap =
            new LinkedMap<>();

    public MultiAssertion(String... names) {
        for (String n : names) {
            assertionMap.put(n, null);
        }
    }

    public Assertion<?> getAssertion(String name) {
        return assertionMap.get(name);
    }

    @Override
    public String toString() {
        return null;
    }

}
