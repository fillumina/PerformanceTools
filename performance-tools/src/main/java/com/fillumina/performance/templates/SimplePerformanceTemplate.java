package com.fillumina.performance.templates;

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
        test(NullPerformanceConsumer.INSTANCE, NullPerformanceConsumer.INSTANCE);
    }

    /**
     * Use in main():
     * <pre><code>
     * public class SomePerformanceTest
     *         extends JUnitPerformanceTestAdvancedTemplate {
     *
     *     public static void main(final String[] args) {
     *         new SomePerformanceTest().testWithOutput();
     *     }
     * </code></pre>
     */
    public void testWithDetailedOutput() {
        test(StringCsvViewer.CONSUMER, StringTableViewer.CONSUMER);
    }

    public void testWithOutput() {
        test(NullPerformanceConsumer.INSTANCE, StringTableViewer.CONSUMER);
    }

    public abstract void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer);
}
