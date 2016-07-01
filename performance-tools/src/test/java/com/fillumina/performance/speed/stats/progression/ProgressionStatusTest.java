package com.fillumina.performance.speed.stats.progression;

import static junit.framework.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionStatusTest {

    @Test
    public void shouldPrintOut() {
        ProgressionStatus ps = new ProgressionStatus("message", 12, 0, null);
        assertNotNull(ps.toString());
    }

}
