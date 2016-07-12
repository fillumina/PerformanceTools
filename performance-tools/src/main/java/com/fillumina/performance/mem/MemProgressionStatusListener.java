package com.fillumina.performance.mem;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemProgressionStatusListener {

    void accepts(int sample,
            int totalSamples,
            String testName,
            long memoryUsed);
}
