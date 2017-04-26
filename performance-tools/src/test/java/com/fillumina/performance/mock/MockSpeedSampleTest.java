package com.fillumina.performance.mock;

import com.fillumina.performance.speed.sample.SpeedSample;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MockSpeedSampleTest {

    @Test
    public void shouldCreateASpeedSample() {
        SpeedSample sample = MockSpeedSample.builder()
                .addTest("first")
                    .timePerOp(10)
                    .iterations(2_000)
                .endTest()
                .addTest("second")
                    .timePerOp(50)
                    .iterations(500)
                .endTest()
                .createSample();

        assertEquals(2_000 * 10 + 50 * 500, sample.getTotalTimeNs());

        assertEquals(10, sample.getMeasure("first").getMean(), 0.1);
        assertEquals(50, sample.getMeasure("second").getMean(), 0.1);

        assertEquals(2_000, sample.getTimeMap().get("first").getIterations());
        assertEquals(500, sample.getTimeMap().get("second").getIterations());
    }
}
