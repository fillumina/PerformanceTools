package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class OldPerformanceTimerTest {

    @Test
    public void shouldExecuteTestAddedToPerformanceTimer() {
        final AtomicBoolean oldTest = new AtomicBoolean(false);
        final AtomicBoolean newTest = new AtomicBoolean(false);

        final PerformanceTimer pt = PerformanceTimerBuilder
                .createSingleThreaded();

        pt.addTest("OLD", new Runnable() {
            @Override
            public void run() {
                oldTest.set(true);
            }
        });

        pt.instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                .setIterationProgression(10)
                .build())
            .addTest("NEW", new Runnable() {

                @Override
                public void run() {
                    newTest.set(true);
                }
            }).execute();

        assertTrue(oldTest.get());
        assertTrue(newTest.get());
    }
}
