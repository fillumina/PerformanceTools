package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StringGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that checks if an {@link Assertable} complies with the
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

    /** @return true if the given {@link Assertable} complies. */
    default boolean satisfy(Assertable assertable) {
        try {
            Assertion.this.check(assertable);
            return true;
        } catch (AssertionError ignored) {
            return false;
        }
    }

    /**
     * Adds itself to the given {@link failedAssertions} if fails.
     *
     * @param assertable            the {@link Assertable} to check
     * @param failedAssertions      failed assertions for each assertable
     * @param unusedAssertionChecker   unchecked assertions (to recognize
     *                              unused assertions)
     */
    default void checkAndReport(Assertable assertable,
            Map<Assertable, List<Assertion>> failedAssertions,
            UnusedAssertionChecker unusedAssertionChecker) {
        try {
            if (!satisfy(assertable)) {
                List<Assertion> list = failedAssertions.get(assertable);
                if (list == null) {
                    list = new ArrayList<>();
                    failedAssertions.put(assertable, list);
                }
                list.add(this);
            }
            unusedAssertionChecker.setUsed(this);
        } catch (MeasureNotFoundException e) {
            unusedAssertionChecker.setUnused(this);
        }
    }

}
