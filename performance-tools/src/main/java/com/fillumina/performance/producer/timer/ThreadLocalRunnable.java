package com.fillumina.performance.producer.timer;

/**
 * Allows to use a local object in each thread (useful to keep track
 * of thread usage).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class ThreadLocalRunnable<T> implements Runnable {

    private final ThreadLocal<T> threadLocal = new ThreadLocal<>();

    /**
     * Creates a thread local object.
     * Note that the creation of the thread local object is counted in the
     * final time so make it fast.
     */
    protected abstract T createThreadLocalObject();

    @Override
    public void run() {
        T localObject = threadLocal.get();
        if (localObject == null) {
            localObject = createThreadLocalObject();
            threadLocal.set(localObject);
        }
        run(localObject);
    }

    public abstract void run(final T localObject);
}
