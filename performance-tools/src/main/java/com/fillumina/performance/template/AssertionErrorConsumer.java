package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AbstractAssertionError;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertionErrorConsumer {

    /** @return true throws the actual exception, false will silent it. */
    boolean consume(AbstractAssertionError err);
}
