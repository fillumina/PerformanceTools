package com.fillumina.performance.infrastructure;

/**
 * Defines a test.
 * <p>
 * There are various event methods which are called in this order:
 * <ol>
 * <li>[once] setUp()
 * <li>[samples] onBeforeSample(iterations)
 * <li>[samples] [iterations] test()
 * <li>[samples] onAfterSample(iterations)
 * <li>[once] tearDown()
 * </ol>
 * The sequence might be eventually repeated.
 * <p>
 * Be aware that the JVM tries hard to optimize its code and this is especially
 * true in case of benchmarks which tends to execute the same code often with
 * the same data over and over. There are various optimizations that can
 * influence the outcome of the benchmark:
 * <ul>
 * <li><b>dead code:</b>
 * the JVM recognizes that some code doesn't have side effects
 * (doesn't influence anything) and simply eliminates it.<br>
 * Solutions:
 * <ul>
 * <li>use one of the methods {@link Sink#drain(Object)} which try to
 * confound the JVM into thinking the given data has side effects;
 * <li>don't use final or static variables which are easily optimized,
 * prefer using the volatile keyword and field instead of local variables;
 * <li>don't call methods with always the same parameters;
 * </ul>
 * <li><b>constant folds:</b>
 * constants and static code are optimized out of the benchmarking loop
 * so a good benchmark should never use them.
 * <li><b>looping:</b>
 * loops (especially for loops) are unfolded and heavily
 * optimized: definitely avoid using them.
 * <li><b>overriding methods:</b>
 * the JVM optimizes the calls to methods if it knows
 * there cannot be ambiguity. In case a method has been overridden it should
 * use virtual pointer to decide which version to call at runtime and this
 * slows down the execution.
 * The trick part is that the execution speed of the code
 * under test might be influenced by other uncorrelated code executed.
 * <li><b>memory access and caching:</b>
 * the memory layout influences memory access and consequently
 * execution speed. If some variables happen to be adjacent in memory their
 * value can be prefetched by modern CPUs and their access be consequently
 * artifically faster or, if they are used in a concurrent exclusive way
 * they might be on the same cache line so if two threads access (and possibly
 * modify) them they constantly invalidate the cache and require a reload
 * significantly slowing down the execution.<br>
 * Solutions:
 * <ul>
 * <li>Separate critical variables by defining a number of other variables
 * between their definitions.
 * <pre><code>
    public static class StatePadded {
        int readOnly;
        int p01, p02, p03, p04, p05, p06, p07, p08;
        int p11, p12, p13, p14, p15, p16, p17, p18;
        int writeOnly;
        int q01, q02, q03, q04, q05, q06, q07, q08;
        int q11, q12, q13, q14, q15, q16, q17, q18;
    }
 * </code></pre>
 * Do the same with classes
 * using the following structure
 * (copied from {@code JMHSample_22_FalseSharing} from
 * <a href='http://openjdk.java.net/projects/code-tools/jmh/'>jmh</a>):
 * <pre><code>
    public static class StateHierarchy_1 {
        int readOnly;
    }

    public static class StateHierarchy_2 extends StateHierarchy_1 {
        int p01, p02, p03, p04, p05, p06, p07, p08;
        int p11, p12, p13, p14, p15, p16, p17, p18;
    }

    public static class StateHierarchy_3 extends StateHierarchy_2 {
        int writeOnly;
    }

    public static class StateHierarchy_4 extends StateHierarchy_3 {
        int q01, q02, q03, q04, q05, q06, q07, q08;
        int q11, q12, q13, q14, q15, q16, q17, q18;
    }
 *</code></pre>
 * <li>Use the array trick: instead of padding with variables use well
 * separated values in an array which is guaranteed to be located on
 * consecutive memory locations.
 * <li>Use @{@link sun.misc.Contended} (only available with JDK 8) and use the
 * start parameter option {@code -XX:-RestrictContended} to enable it.
 * </ul>
 * </ul>
 * Most of the problems expressed here are difficult to solve apart from
 * fighting hard against the JVM. But consider that there are a number of them,
 * each working in a different hardware architecture and with possible different
 * memory managers that to foreseeing every possible optimizations is nearly
 * impossible.
 * JMH tries to do that, and it does a very good job at it.
 * <br>
 * This framework takes a different approach: it tries to not be fouled by a bad
 * benchmark (i.e. it throws an exception if it detects that a code has been
 * evicted) but it stays within the boudaries of the java world so to leave
 * to the benchmark writer the burden to craft a good one. Benchmarking is
 * not an easy task, it must be pursued iteratively, by experiments, trying
 * to avoid pitfalls both with the algorithm under test and with the JVM it
 * runs on. It involves a deep understanding of the JVM environment and its
 * optimizations and this framework aims to provide a set of tools to help
 * investigate and writing good benchmarks.
 *
 * @author Francesco Illuminati
 */
public abstract class Testable extends Sink {

    private int nested;

    boolean innerSetUp() {
        boolean execute = nested == 0;
        if (execute) {
            setUp();
        }
        nested++;
        return execute;
    }

    boolean innerTearDown() {
        nested--;
        boolean execute = nested == 0;
        if (execute) {
            tearDown();
        }
        return execute;
    }

    /** Tear down no matter what. */
    void innerTearDownOnException() {
        nested = 0;
        tearDown();
    }

    /**
     * Fast test with no memory used or allocated. Use as baseline.
     */
    public static final Testable DO_NOTHING = new Testable() {
        private volatile int counter = 0;
        @Override public void test() {counter++;}
        public int getCounter() {return counter;}
    };

    /**
     * Called at every initialization of the test (might be more than once,
     * i.e. if warmup is required). Its execution time is not accounted.
     */
    public void setUp() {}

    /**
     * Called before every sample (number of iterations accounted for a single
     * measure) of {@link #test()}, its execution time is not accounted.
     *
     * @param iterations number of iterations to be performed.
     */
    public void onBeforeSample(int iterations) {}

    /**
     * Executes the test for the number of iterations specified in
     * {@link #onBeforeSample(int) }.
     * <p>
     * To avoid dead code eviction use one of the {@link Sink#drain(Object)}
     * methods.
     */
    public abstract void test();

    /**
     * Executed after the sample.
     *
     * @param iterations executed
     */
    public void onAfterSample(int iterations) {}

    /**
     * Executed when test is done. Can be called more than once but always
     * after {@link #setUp() }.
     */
    public void tearDown() {}
}
