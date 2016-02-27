package com.fillumina.performance.producer.suite;

/**
 *
 * @param P the passed parameter
 * @param T the local object
 *
 * @author Francesco Illuminati
 */
public abstract class ThreadLocalParametrizedRunnable<T,P>
        extends ParametrizedTestable<P> {
    private final ThreadLocal<T> threadLocal = new ThreadLocal<>();

    /**
     * Note that the creation of the local object is accounted in the
     * test time.
     */
    protected abstract T createLocalObject();

    @Override
    public Object test(final P param) {
        T localObject = threadLocal.get();
        if (localObject == null) {
            localObject = createLocalObject();
            threadLocal.set(localObject);
        }
        return test(localObject, param);
    }

    /**
     * Contains the test. It's a sink so that every object returned is checked
     * making the code to calculate it not removable by the JVM optimization.
     */
    public abstract Object test(T localObject, P param);
}
