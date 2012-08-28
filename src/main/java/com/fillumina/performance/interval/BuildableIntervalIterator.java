package com.fillumina.performance.interval;

import java.util.Iterator;

/**
 *
 * @author fra
 */
public interface BuildableIntervalIterator<T> extends Iterator<T> {

    void setFirst(final T first);

    void setLast(final T last);

    void setStep(final T step);
}
