package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertableConsumer<A extends Assertable>
        implements AssertableConsumer<A>{

    private final Class<A> acceptedClazz;

    public AbstractAssertableConsumer(Class<A> acceptedClazz) {
        this.acceptedClazz = acceptedClazz;
    }

    @Override
    public Class<A> getAcceptedAssertableClass() {
        return acceptedClazz;
    }

}
