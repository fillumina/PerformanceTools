package com.fillumina.performance.infrastructure;

import com.fillumina.performance.infrastructure.annotation.BeforeSample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class MemTestable implements Runnable {
    private Object[] array;
    private int index;

    public abstract Object memTest();

    @BeforeSample
    public void onBeforeSample(int iterations) {
        array = new Object[iterations];
        index = 0;
    }

    @Override
    public void run() {
        Object obj = memTest();
        array[index] = obj;
        index++;
    }
}
