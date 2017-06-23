package com.fillumina.performance.time;

import com.fillumina.performance.time.CpuBurner;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CpuBurnerTest {

    public static void main(final String[] args) {
        CpuBurner.INSTANCE.burnSeconds(15);
    }
}
