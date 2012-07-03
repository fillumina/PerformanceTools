package com.fillumina.performance.utils;

import java.util.Iterator;

/**
 *
 * @author fra
 */
public interface BuildableIntervalIterator<T> extends Iterator<T> {

    void setIndex(final T index);

    void setLast(final T last);

    void setStep(final T step);

}
