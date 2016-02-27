package com.fillumina.performance.producer.timer;

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
        final int items = getItems();
        objects = (T[]) new Object[items];
        for (int i=0; i<items; i++) {
            objects[i] = createTestObject();
        }
        values = createTestValues();
    }

    @Override
    public void beforeTest() {
        for (int i=0, len=objects.length; i<len; i++) {
            beforeTest(objects[i], values);
        }
    }

    @Override
    public Object test() {
        return test(objects[counter++]);
    }

    protected abstract int getItems();

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
