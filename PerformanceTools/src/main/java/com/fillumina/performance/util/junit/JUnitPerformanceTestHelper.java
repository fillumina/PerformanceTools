package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author fra
 */
@RunWith(JUnitPerformanceTestRunner.class)
public abstract class JUnitPerformanceTestHelper {

    @Test
    public void boundaryCheckAgainstOOBExceptionTest() {
        test(NullPerformanceConsumer.INSTANCE, NullPerformanceConsumer.INSTANCE);
    }

    protected void testWithOutput() {
        test(StringCsvViewer.CONSUMER, StringTableViewer.CONSUMER);
    }

    public abstract void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer);
}
