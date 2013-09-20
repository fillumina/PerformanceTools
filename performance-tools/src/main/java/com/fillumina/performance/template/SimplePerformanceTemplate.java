package com.fillumina.performance.template;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class SimplePerformanceTemplate {

    public void testWithoutOutput() {
        executePerformanceTest(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    /**
     * Use in {@code main()}:
     * <pre><code>
     *     public static void main(final String[] args) {
     *         new SomePerformanceTest().testWithDetailedOutput();
     *     }
     * ...
     * </code></pre>
     */
    public void testWithDetailedOutput() {
        executePerformanceTest(StringCsvViewer.INSTANCE,
                StringTableViewer.INSTANCE);
    }

    public void testWithOutput() {
        executePerformanceTest(NullPerformanceConsumer.INSTANCE,
                StringTableViewer.INSTANCE);
    }

    public abstract void executePerformanceTest(
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer);
}
