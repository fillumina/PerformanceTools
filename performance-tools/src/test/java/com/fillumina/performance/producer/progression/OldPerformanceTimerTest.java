package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.producer.timer.AbstractTestable;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class OldPerformanceTimerTest {

    @Test
    public void shouldExecuteTestAddedToPerformanceTimer() {
        final AtomicBoolean oldTest = new AtomicBoolean(false);
        final AtomicBoolean newTest = new AtomicBoolean(false);

        final PerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded();

        pt.addTest("OLD", new AbstractTestable() {
            @Override
            public Object test() {
                oldTest.set(true);
                return null;
            }
        });

        pt.instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                .setIterationProgression(10)
                .build())
            .addTest("NEW", new AbstractTestable() {

                @Override
                public Object test() {
                    newTest.set(true);
                    return null;
                }
            }).execute();

        assertTrue(oldTest.get());
        assertTrue(newTest.get());
    }
}
