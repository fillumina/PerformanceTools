package com.fillumina.performance.mem;

import com.fillumina.performance.util.StaticPath;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemProgressionStatusListener {

    void accepts(StaticPath fullTestName,
            int sample,
            int totalSamples,
            String testName,
            long memoryUsed);
}
