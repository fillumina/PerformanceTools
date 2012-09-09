package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class PerformanceTestHelperTest extends PerformanceTestHelper {

    @Override
    public void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {
        assertTrue(iterationConsumer instanceof NullPerformanceConsumer);
        assertTrue(resultConsumer instanceof NullPerformanceConsumer);
    }
}
