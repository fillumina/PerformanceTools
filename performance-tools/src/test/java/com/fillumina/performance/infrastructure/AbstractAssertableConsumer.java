package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertableConsumer<A extends Assertable>
        implements Consumer<A>{

    private final Class<A> acceptedClazz;

    public AbstractAssertableConsumer(Class<A> acceptedClazz) {
        this.acceptedClazz = acceptedClazz;
    }

    @Override
    public Class<A> getAcceptedAssertableClass() {
        return acceptedClazz;
    }

}
