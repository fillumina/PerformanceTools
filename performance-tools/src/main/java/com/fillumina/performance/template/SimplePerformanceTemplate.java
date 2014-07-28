package com.fillumina.performance.template;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;

/**
 * Template with some simple viewers wired in.
 *
 * @author Francesco Illuminati
 */
public abstract class SimplePerformanceTemplate {

    /**
     * Executes the test without any output.
     * This method name starts with test so that it's automatically executed by
     * old JUnit versions (previous than 4.x).
     */
    public void testWithoutOutput() {
        executePerformanceTest(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    /**
     * Use in {@code main()}:
     * <pre><code>
     *     public static void main(final String[] args) {
     *         new SomePerformanceTest().executeWithIntermediateOutput();
     *     }
     * ...
     * </code></pre>
     * Produces output even for intermediate steps. It can be verbose.
     */
    public void executeWithIntermediateOutput() {
        executePerformanceTest(StringCsvViewer.INSTANCE,
                StringTableViewer.INSTANCE);
    }

    /**
     * Prints out only the final result of the test without result per
     * iteration.
     */
    public void executeWithOutput() {
        executePerformanceTest(NullPerformanceConsumer.INSTANCE,
                StringTableViewer.INSTANCE);
    }

    /** Defines the performance test. */
    public abstract void executePerformanceTest(
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer);
}
