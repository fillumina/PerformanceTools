package com.fillumina.performance.mem;

import com.fillumina.performance.suite.ParameterizedTestable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class MemParameterizedTestable<T>
        extends ParameterizedTestable<T> {

    private Object[] array;
    private int index;

    public abstract Object memTest(T param);

    @Override
    public void onBeforeSample(T param, int iterations) {
        array = new Object[iterations];
        index = 0;
    }

    @Override
    public Object test(T param) {
        Object obj = memTest(param);
        array[index] = obj;
        index++;
        return obj;
    }

}
