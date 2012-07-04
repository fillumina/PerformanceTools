package com.fillumina.performance.examples;

import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.sequence.ProgressionSequence;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import org.junit.Test;

/**
 *
 * @author fra
 */
public class InstanceOfPerformanceTest {

    @Test
    public void shouldClassCheckBeFasterThanInstanceOf() {
        final Object object = new InstanceOfPerformanceTest();
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("instanceof", new Runnable() {

            @Override
            public void run() {
                if (!(object instanceof InstanceOfPerformanceTest)) {
                    throw new RuntimeException();
                }

            }
        });

        // it's 67% FASTER on the long run!!
        pt.addTest("classcheck", new Runnable() {

            @Override
            public void run() {
                if (!Object.class.isAssignableFrom(InstanceOfPerformanceTest.class)) {
                    throw new RuntimeException();
                }

            }
        });

        new ProgressionSequence(pt)
//                .setPerformanceConsumer(new StringTableViewer())
                .sequence(100_000, 3, 10);
    }
}
