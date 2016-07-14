package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ComposedName;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionCondition<A extends AssertableMultiStats>
        implements Assertion<A> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Assertion<AssertableMultiStats>> getLeaves(
            ComposedName name) {
        return Collections.singletonList((Assertion<AssertableMultiStats>)this);
    }
}
