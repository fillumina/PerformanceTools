package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class MemTestable extends Testable {
    private Object[] array;
    private int index;

    public abstract Object memTest();

    @Override
    public void onBeforeSample(int iterations) {
        array = new Object[iterations];
        index = 0;
    }

    @Override
    public void test() {
        Object obj = memTest();
        array[index] = obj;
        index++;
    }
}
