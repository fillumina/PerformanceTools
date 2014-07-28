package com.fillumina.performance;

import com.fillumina.performance.producer.timer.MultiThreadPerformanceExecutorBuilder;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.SingleThreadPerformanceExecutor;

/**
 * Static factory helper to create a {@link PerformanceTimer}.
 * <p>
 * There are two ways of using this API:
 * <ul>
 * <li>Using a
 * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>fluent
 * interface</a></i> that starts by creating the needed
 * {@link PerformanceTimer} using this static factory (or by constructing
 * one directly);</li>
 * <li>Using one of the templates in the
 * {@link com.fillumina.performance.template} package.</li>
 * </ul>
 * The first choice allows for better customizations while the second is
 * probably easier to use.
 *
 * @see <a href='http://www.ibm.com/developerworks/java/library/j-jtp02225/index.html'>
 *      Java theory and practice: Anatomy of a flawed microbenchmark
 *      (Brian Goetz)
 *      </a>
 *
 * @see <a href='http://www.ibm.com/developerworks/java/library/j-jtp12214/#4.0'>
 *      Java theory and practice: Dynamic compilation and performance measurement
 *      (Brian Goetz)
 *      </a>
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimerFactory {

    /**
     * Creates a single threaded performance test.
     * <pre>
    PerformanceTimerFactory
       .createSingleThreaded()

       .addTest("one", new Runnable() {
           public void run() {
               // define here the test
           }
       })
       .addTest("two", new Runnable() {
           public void run() {
               // define here the test
           }
       })

       .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
           .setMaxStandardDeviation(1)
           .buildMultiThreadPerformanceExecutor())

       .execute()
       .use(AssertPerformance.withTolerance(5F)
                .assertTest("one").sameAs("two"));
     * </pre>
     */
    public static PerformanceTimer createSingleThreaded() {
        return new PerformanceTimer(new SingleThreadPerformanceExecutor());
    }

    /**
     * Creates a {@link PerformanceTimer} with a multi threaded executor
     * using a builder (don't forget to call
     * {@link MultiThreadPerformanceExecutorBuilder#build()}
     * at the end of the builder).
     * Each test will be executed in a multi threaded
     * environment (so take extra care about thread safety, especially with
     * the test's fields).
     * <pre>
    PerformanceTimerFactory.getMultiThreadedBuilder()
            .setConcurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build()

            .addTest("one", new Runnable() {
                public void run() {
                    // define here the test
                }
            })
            .addTest("two", new Runnable() {
                public void run() {
                    // define here the test
                }
            })

            .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                .setMaxStandardDeviation(3)
                .buildMultiThreadPerformanceExecutor())

            .execute()
            .use(AssertPerformance.withTolerance(5F)
                .assertTest("one").sameAs("two"));
     * </pre>
     */
    public static MultiThreadPerformanceExecutorBuilder getMultiThreadedBuilder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }
}
