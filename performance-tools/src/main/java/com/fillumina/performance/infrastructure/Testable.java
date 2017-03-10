package com.fillumina.performance.infrastructure;

/**
 * Defines a test.
 *
 * @author Francesco Illuminati
 */
public interface Testable {

    /**
     * Fast test with no memory used or allocated. Use as baseline.
     */
    Testable DO_NOTHING = new Testable() {
        private int counter = 0;
        @Override public void setUp() {}
        @Override public void onBeforeSample(int iterations) {}
        @Override public void test() {counter++;}
        public int getCounter() {return counter;}
    };

    /** Called at every initialization of the test (might be more than once). */
    void setUp();

    /**
     * Called before every sample (number of iterations accounted for a single
     * measure) of {@link #test()}, its execution time is not accounted.
     *
     * @param iterations number of iterations to be performed.
     */
    void onBeforeSample(int iterations);

    /**
     * Executes the test for the number of iterations specified in
     * {@link #onBeforeSample(int) }.
     *
     * @return the result of the operation under test so the code
     *  related to it will not be evicted by JVM the dead code optimization.
     */
    void test();
}
