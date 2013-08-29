package com.fillumina.performance.producer.progression;

import org.junit.Test;

/**
 *
 * @author fra
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
                    .setMaxStandardDeviation(7)
                    .build();

        instrumenter.execute();
    }
}
