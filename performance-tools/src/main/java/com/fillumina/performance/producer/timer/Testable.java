package com.fillumina.performance.producer.timer;

/**
 * It's a {@link Runnable} that avoids dead code eviction by the JVM taking
 * care of the results of the tested calculation in a way that the calculation
 * itself will not be evicted.
 *
 * @author Francesco Illuminati
 */
public interface Testable {

    /** Called once when initializing the test. */
    void setUp();

    /**
     * Executed before every execution of {@link #test()}, its time is not
     * accounted.
     */
    void beforeTest();

    /**
     * Executes the test.
     * @return the result of the operation under test so the code
     *  related to it will not be evicted by the dead code optimization of
     *  the JVM.
     */
    public abstract Object test();

//    private static final Object REFERENCE = new Object();
//    @Override
//    public void run() {
//        final Object obj = test();
//        // force obj to be evaluated and so the code returning it
//        // will not be evicted. The comparation is always false.
//        if (obj == REFERENCE) {
//            throw new IllegalStateException();
//        }
//    }
}
