package com.fillumina.performance.producer.suite;

/**
 *
 * @param P the passed parameter
 * @param T the local object
 *
 * @author fra
 */
public abstract class ThreadLocalParametrizedRunnable<T,P>
        extends ParametrizedRunnable<P> {

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
        call(localObject, param);
    }

    /** Contains the test. */
    public abstract void call(T localObject, P param);
}
