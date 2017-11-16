package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StringGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that checks if the statistics comply with the
 * requirements.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertion
        extends Consumer<Assertable>,
                StringGenerator<Assertable> {

    /** It's a more meaningful name for {@link #accept(Assertable)}. */
    default void check(Assertable assertable) throws AssertionError {
        accept(assertable);
    }

    default boolean satisfy(Assertable assertable) {
        try {
            Assertion.this.check(assertable);
            return true;
        } catch (AssertionError err) {
            return false;
        }
    }

    /**
     *
     * @param assertable            the assertable to check against
     * @param failedAssertions      failed assertions for each assertable
     * @param checkedAssertionMap   unchecked assertions (to recognize wrong
     *                              tests)
     */
    default void check(Assertable assertable,
            Map<Assertable, List<Assertion>> failedAssertions,
            Map<Assertion, Boolean> checkedAssertionMap) {
        try {
            if (!satisfy(assertable)) {
                List<Assertion> list = failedAssertions.get(assertable);
                if (list == null) {
                    list = new ArrayList<>();
                    failedAssertions.put(assertable, list);
                }
                list.add(this);
            }
            checkedAssertionMap.put(this, Boolean.TRUE);
        } catch (TestNotFoundException e) {
            if (!checkedAssertionMap.containsKey(this)) {
                checkedAssertionMap.put(this, Boolean.FALSE);
            }
        }
    }

}
