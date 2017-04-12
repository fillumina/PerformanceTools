package com.fillumina.performance.infrastructure;

/**
 * Defines a test.
 * <p>
 * There are various event methods which are called in this order:
 * <ol>
 * <li>[once] setUp()
 * <li>[samples] onBeforeSample(iterations)
 * <li>[samples * iterations] test()
 * <li>[samples] onAfterSample(iterations)
 * <li>[once] tearDown()
 * </ol>
 * The sequence might be eventually repeated for warmup and testing.
 * <p>
 * Benchmarking is not a trivial job: there are a number of pitfalls that
 * can make a test completely unreliable. Writing a good benchmark is an
 * iterative task made of experiments and data analysis: an algorithm should be
 * tested on different conditions to try to extract a meaningful profile of its
 * performances. Different algorithms have different characteristics
 * (average, weak and strong spots) that should be analyzed carefully before
 * exposing bare execution times.
 * That will never be an automatic task in the same way as unit testing will
 * never be.
 * <p>
 * Remember that no test can be
 * completely accurate and the same code might run differently on different
 * systems and configurations. The main point is to match two or more
 * similar algorithms on the same arena (which most of the time we cannot be
 * picky about) and check their relative speed. That's
 * the information I value most: how faster or slower a modification make
 * my code in respect to a baseline (previous version or different one).
 * Ratios tend to stay stable even on different systems and the notion
 * that code A is 13.2 % faster than code B adds more value than their
 * respective execution times. This is not a trivial point, this framework is
 * built  around this consideration: compare codes rather than give exact,
 * 'distilled', out of the world execution times.
 * A strong statistical analysis make the data useful and reliable.
 * You can compare them in all the environments you need easily, check the
 * results and even assert them in unit tests.
 * <p>
 * Executing a code continously (synthetic benchmarking) might not always be
 * the best estimation of its execution time on a real program: while under
 * benchmark the code will resides on the cache and the JVM will do its best
 * to optimize it. The same code executed once or twice per second would
 * be much slower: its data and code must be always fetched from memory (so an
 * over-complicated code will always be slower than a simple but more
 * compact one on this scenario), its code would probably not be compiled
 * by JIT and cached.
 * <p>
 * Be aware that the JVM tries hard to optimize the running code and this can
 * especially effects benchmarks which tend to execute the same code often with
 * the same data over and over. There are various optimizations that can
 * influence the outcome of a benchmark:
 * <ul>
 * <li><b>JIT:</b>
 * On first executions the code will be interpreted rather than compiled
 * by the JVM, after some more executions the code will be progressively
 * compiled and optimized. The execution time will change accordingly to the
 * actual state of the code (interpreted, compiled, optimized). A benchmark
 * measure the speed of a code by executing it continuously and timing its
 * performances. In this scenario the JVM is strongly suggested to optimize
 * it at its best. That would not be the same case of real live code where the
 * same algorithm can be executed rarely.
 * For this reason a possibly over-optimized, huge, performance wise
 * code could actually run (much) slower than a simpler one just because
 * it's not compiled at all (and to interpret a huge code costs time).
 * <li><b>dead code:</b>
 * If the JVM detects that some code doesn't have side effects
 * (doesn't influence anything) it might simply eliminate it.<br>
 * Solutions:
 * <ul>
 * <li>use one of the methods {@link Sink#drain(Object)} which try to
 * confound the JVM into thinking the given data has side effects;
 * <pre><code>
 * new Testable() {
 *      volatile int counter;
 *      void test() {
 *          Sink.drain(counter++);
 *      }
 * }
 * </code></pre>
 * <li>don't use final or static variables which are easily optimized,
 * prefer using the volatile keyword and fields instead of local variables;
 * <li>don't call methods with always the same parameters, try to use
 * a range of different values preferably randomly chosen at runtime so to
 * avoid patterns (you may use
 * {@link com.fillumina.performance.util.rnd.XSPRandom} as an efficient and very
 * fast pseudo random number generator);
 * <pre><code>
 * new Testable() {
 *      XSPRandom rnd = new XSPRandom();
 *      void test() {
 *          Sink.drain(Math.sin(rnd.nextDouble());
 *      }
 * }
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
// TODO review comment (we use java 8)
// TODO make it usable with a () ->
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

    long innerIterate(int iterations) {
        long time = System.nanoTime();
        for (int i=0; i<iterations; i++) {
            test();
        }
        return System.nanoTime() - time;
    }

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
