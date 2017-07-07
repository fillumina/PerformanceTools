package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerfType {

    Class<? extends Assertable> getAssertableClass();
}
