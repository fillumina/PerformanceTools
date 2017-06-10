package com.fillumina.performance.util;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
@Deprecated //TODO not used
public interface TimeLimited {

    TimeLimited setTimeout(final long timeout, final TimeUnit unit);
}
