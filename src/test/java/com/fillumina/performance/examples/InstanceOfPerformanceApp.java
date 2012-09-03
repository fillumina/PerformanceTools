package com.fillumina.performance.examples;

import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.instrumenter.ProgressionPerformanceInstrumenter;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class InstanceOfPerformanceApp {

    public static void main(final String[] args) {
        new InstanceOfPerformanceApp().shouldClassCheckBeFasterThanInstanceOf();
    }

    public void shouldClassCheckBeFasterThanInstanceOf() {
        final Object object = new InstanceOfPerformanceApp();
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("instanceof", new Runnable() {

            @Override
            public void run() {
                if (!(object instanceof InstanceOfPerformanceApp)) {
                    throw new RuntimeException();
                }

            }
        });

        // it's 67% FASTER on the long run!!
        pt.addTest("classcheck", new Runnable() {

            @Override
            public void run() {
                if (!Object.class.isAssignableFrom(InstanceOfPerformanceApp.class)) {
                    throw new RuntimeException();
                }

            }
        });

        pt.instrumentedBy(new ProgressionPerformanceInstrumenter.Builder())
                .setTimeout(10, TimeUnit.MINUTES)
                .setBaseAndMagnitude(10_000_000, 3)
                .setSamplePerIterations(10)
                .build()
                .addPerformanceConsumer(StringTableViewer.CONSUMER)
                .executeSequence();
    }
}
