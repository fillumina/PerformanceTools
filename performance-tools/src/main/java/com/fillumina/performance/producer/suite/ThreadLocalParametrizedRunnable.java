package com.fillumina.performance.producer.suite;

/**
 *
 * @param P the passed parameter
 * @param T the local object
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class ThreadLocalParametrizedRunnable<T,P>
        extends ParametrizedRunnable<P> {
    private static final Object REFERENCE = new Object();

    private final ThreadLocal<T> threadLocal = new ThreadLocal<>();

    /**
     * Note that the creation of the local object is counted in the
     * final time.
     */
    protected abstract T createLocalObject();

    @Override
    public void call(final P param) {
        T localObject = threadLocal.get();
        if (localObject == null) {
            localObject = createLocalObject();
            threadLocal.set(localObject);
        }
        final Object result = call(localObject, param);
        if (result == REFERENCE) {
            throw new AssertionError();
        }
    }

    /**
     * Contains the test. It's a sink so that every object returned is checked
     * making the code to calculate it not removable by the JVM optimization.
     */
    public abstract Object call(T localObject, P param);
}
