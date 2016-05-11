package com.fillumina.performance.progression;

import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.stats.progression.ProgressionPerformanceInstrumenter;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class InstrumenterNullInstrumentableTest {

    @Test(expected = IllegalStateException.class)
    public void shouldProgressionPerformanceInstrumenterCheckNullInstrumentable() {
        final ProgressionPerformanceInstrumenter instrumenter =
                ProgressionPerformanceInstrumenter.builder()
                    .setBaseAndMagnitude(10, 1)
                    .build();

        instrumenter.execute();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldAutoProgressionPerformanceInstrumenterCheckNullInstrumentable() {
        final AutoProgressionPerformanceInstrumenter instrumenter =
                AutoProgressionPerformanceInstrumenter.builder()
                    .setMinConfidence(0.9)
                    .build();

        instrumenter.execute();
    }
}
