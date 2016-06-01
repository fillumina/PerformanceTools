package com.fillumina.performance.sample;

/**
 * Testing a complex operation on objects can be difficult because the state
 * of the object might have changed as a result making it non repeatable.
 * But to measure an operation with some sort of precision multiple iterations
 * are needed (the system timer are not very reliable for the very short time
 * a single operation might take).
 * To overcome this problem the same operation can be
 * performed on a collection of objects of the same type. This class takes
 * this approach.
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
    public void onBeforeSample(int iterations) {
        if (objects == null || iterations != objects.length) {
            objects = (T[]) new Object[iterations];
            for (int i=0; i<iterations; i++) {
                objects[i] = createTestObject();
            }
        }
        values = createTestValues();
        for (int i=0, len=objects.length; i<len; i++) {
            beforeSample(objects[i], values);
        }
        counter = 0;
    }

    @Override
    public Object test() {
        final Object result = test(objects[counter]);
        counter++;
        return result;
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
    protected abstract void beforeSample(T t, V v);

    /** Actually test the object. */
    protected abstract Object test(T t);
}
