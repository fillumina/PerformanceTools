package com.fillumina.performance.producer;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public interface TimeLimited {

    TimeLimited setTimeout(final long timeout, final TimeUnit unit);
}
