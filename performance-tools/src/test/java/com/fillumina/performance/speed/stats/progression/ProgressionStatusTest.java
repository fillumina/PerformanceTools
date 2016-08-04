package com.fillumina.performance.speed.stats.progression;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionStatusTest {

    @Test
    public void shouldPrintOut() {
        SampleProgressionStatus ps =
                new SampleProgressionStatus("message", 12, 33, 0,
                        new int[]{101, 102, 103}, null, null);
        assertNotNull(ps.toString());
    }

}
