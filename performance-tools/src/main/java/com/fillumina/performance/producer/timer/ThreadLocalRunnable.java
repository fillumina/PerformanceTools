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
     * Note that the creation of the local object is counted in the
     * final time so make it fast.
     */
    protected abstract T createLocalObject();

    @Override
    public void run() {
        T localObject = threadLocal.get();
        if (localObject == null) {
            localObject = createLocalObject();
            threadLocal.set(localObject);
        }
        run(localObject);
    }

    public abstract void run(final T localObject);
}
