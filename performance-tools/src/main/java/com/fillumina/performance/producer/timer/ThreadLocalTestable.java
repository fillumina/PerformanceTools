package com.fillumina.performance.producer.timer;

/**
 * Allows to use a local object in each thread (useful to keep track
 * of thread usage).
 *
 * @author Francesco Illuminati
 */
public abstract class ThreadLocalTestable<T> extends AbstractTestable {

    private final ThreadLocal<T> threadLocal = new ThreadLocal<>();

    /**
     * Creates a thread local object.
     * Note that the creation of the thread local object is counted in the
     * final time so make it fast.
     */
    protected abstract T createThreadLocalObject();

    @Override
    public Object test() {
        T localObject = threadLocal.get();
        if (localObject == null) {
            localObject = createThreadLocalObject();
            threadLocal.set(localObject);
        }
        return test(localObject);
    }

    public abstract Object test(final T localObject);
}
