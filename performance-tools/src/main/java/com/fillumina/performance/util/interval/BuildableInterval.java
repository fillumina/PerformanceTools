package com.fillumina.performance.util.interval;

import java.util.Iterator;

/**
 *
 * @author Francesco Illuminati
 */
public interface BuildableInterval<T> extends Iterator<T> {

    void setFirst(final T first);

    void setLast(final T last);

    void setStep(final T step);
}
