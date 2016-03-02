package com.fillumina.performance.executor;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class BulkTestable<T,V> implements Testable {
    private T[] objects;
    private V values;
    private int counter;

    @Override
    @SuppressWarnings("unchecked")
    public void setUp() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void beforeTest(int iterations) {
        if (objects == null || iterations != objects.length) {
            objects = (T[]) new Object[iterations];
            for (int i=0; i<iterations; i++) {
                objects[i] = createTestObject();
            }
        }
        values = createTestValues();
        for (int i=0, len=objects.length; i<len; i++) {
            beforeTest(objects[i], values);
        }
    }

    @Override
    public Object test() {
        counter = ((counter + 1) % objects.length);
        return test(objects[counter]);
    }

    /**
     * Creates the object to test. It can be called several times
     * before actual test execution to create all the needed objects.
     */
    protected abstract T createTestObject();

    /**
     * Creates the values to be passed to the various objects before the
     * test execution.
     */
    protected abstract V createTestValues();

    /**
     * Called for each created object before the execution of the test.
     * @param t the test object
     * @param v the value used to prepare the object for the test
     */
    protected abstract void beforeTest(T t, V v);

    /** Actually test the object. */
    protected abstract Object test(T t);
}
