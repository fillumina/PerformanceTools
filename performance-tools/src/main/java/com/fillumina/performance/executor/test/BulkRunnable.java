package com.fillumina.performance.executor.test;

import com.fillumina.performance.executor.annotation.BeforeSample;

/**
 * A test is often repeated and measured many times in order to improve the
 * precision of the measure but sometimes this technique cannot be employed
 * because the state of the object changes as a result of the test itself
 * making it non repeatable.
 * A benchmark that estimate the speed of removing an element from a map
 * filled at 50% cannot be realized without some clever trick.
 * Adding the element after deletion would take time that will be wrongly
 * accounted into the deletion. Adding it in a different method
 * would make the measure limited to only 1 iteration which is often
 * insufficient to reach a decent accuracy.
 * To overcome this problem the same operation can be
 * performed on a collection of objects of the same type.
 * This class helps taking this approach.
 * <p>
 * Remember to specify <code>setBulkSpecificConfig()</code> in the
 * speed configuration.
 *
 * @param T type of the object to run
 * @param V type of the value to be passed
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class BulkRunnable<T,V> implements Runnable {
    private T[] objects;
    private V value;
    private int counter;

    @BeforeSample
    @SuppressWarnings("unchecked")
    public final void onBeforeSample(int iterations) {
        objects = (T[]) new Object[iterations];
        for (int i=0; i<iterations; i++) {
            objects[i] = createTestObject();
        }
        value = createTestValue();
        for (int i=0, len=objects.length; i<len; i++) {
            onBeforeSample(objects[i], value);
        }
        counter = 0;
    }

    @Override
    public final void run() {
        test(objects[counter]);
        counter++;
    }

    /**
     * Creates the object to run. It can be called several times
     * before actual run execution to create all the needed objects.
     */
    public abstract T createTestObject();

    /**
     * Creates the values to be passed to the various objects before the
     * run execution.
     */
    public abstract V createTestValue();

    /**
     * Initializes the object before the execution of the run.
     *
     * @param t the run object
     * @param v the value used to prepare the object for the run
     */
    public abstract void onBeforeSample(T t, V v);

    /**
     * Actually run the object
     * @param t the object to run
     */
    public abstract void test(T t);
}
