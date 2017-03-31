package com.fillumina.performance.speed;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HeatDetectorTest {

    public static void main(final String[] args) {
        System.out.println("initializing...");
        HeatDetector.INSTANCE.init();

        System.out.println("burning...");
        CpuBurner.INSTANCE.burnSeconds(10);

        System.out.println("checking...");
        assertTrue(HeatDetector.INSTANCE.isHeated());
    }
}
