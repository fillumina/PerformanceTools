package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import org.junit.Test;

/**
 *
 * @author fra
 */
public abstract class JUnitPerformanceTestHelper {

    @Test
    public void executeTest() {
        test(NullPerformanceConsumer.INSTANCE, NullPerformanceConsumer.INSTANCE);
    }

    /**
     * Use in the main():
     * <pre><code>
     *
     * </code></pre>
     */
    protected void testWithOutput() {
        test(StringCsvViewer.CONSUMER, StringTableViewer.CONSUMER);
    }

    public abstract void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer);
}
