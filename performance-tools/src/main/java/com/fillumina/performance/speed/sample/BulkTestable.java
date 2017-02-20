package com.fillumina.performance.speed.sample;

/**
 * A test is often repeated and measured many times in order to improve the
 * precision of the measure but sometimes this technique cannot be employed
 * because the state of the object changes as a result of the test making it non
 * repeatable.
 * To overcome this problem the same operation can be
 * performed on a collection of objects of the same type. This class takes
 * this approach.
 * <p>
 * Remember to use the following settings in
 * {@link com.fillumina.performance.template.SpeedConfiguration}:
 * <ul>
 * <li>setSamplesPerStep(100);
 * <li>setIncrementSamples();
 * <li>setGarbageCollectorMillis(100);
 * <li>setTimeout(2, TimeUnit.MINUTES);
 * </ul>
 * Or the all-comprising <code>setBulkSpecificConfig()</code>
 *
 * @param T type of the object to test
 * @param V type of the value to be passed
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class BulkTestable<T,V> implements Testable {
    private T[] objects;
    private V value;
    private int counter;

    @Override
    public void setUp() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBeforeSample(int iterations) {
        if (objects == null || iterations != objects.length) {
            objects = (T[]) new Object[iterations];
            for (int i=0; i<iterations; i++) {
                objects[i] = createTestObject();
            }
        }
        value = createTestValues();
        for (int i=0, len=objects.length; i<len; i++) {
            beforeSample(objects[i], value);
        }
        counter = 0;
    }

    @Override
    public final Object test() {
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
