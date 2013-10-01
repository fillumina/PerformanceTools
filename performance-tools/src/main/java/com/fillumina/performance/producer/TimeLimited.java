package com.fillumina.performance.producer;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public interface TimeLimited {

    TimeLimited setTimeout(final long timeout, final TimeUnit unit);
}
