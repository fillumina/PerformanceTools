package com.fillumina.performance.mem;

import com.fillumina.performance.suite.ParameterizedSequenceTestable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class MemParameterizedSequenceTestable<P,S>
        extends ParameterizedSequenceTestable<P, S>{

    private Object[] array;
    private int index;

    public abstract Object memTest(P param, S sequence);

    @Override
    public void onBeforeTest(P param, S sequence, int iterations) {
        array = new Object[iterations];
        index = 0;
    }

    @Override
    public Object test(P param, S sequence) {
        Object obj = memTest(param, sequence);
        array[index] = obj;
        index++;
        return obj;
    }

}
