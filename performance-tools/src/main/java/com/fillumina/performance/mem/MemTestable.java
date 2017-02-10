package com.fillumina.performance.mem;

import com.fillumina.performance.speed.sample.AbstractTestable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class MemTestable extends AbstractTestable {
    private Object[] array;
    private int index;

    public abstract Object memTest();

    @Override
    public void onBeforeSample(int iterations) {
        array = new Object[iterations];
        index = 0;
    }

    @Override
    public Object test() {
        Object obj = memTest();
        array[index] = obj;
        index++;
        return obj;
    }

}
