package com.fillumina.performance.mem;

import com.fillumina.performance.util.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemProgressionStatusListener {

    void accepts(TName fullTestName,
            int sample,
            int totalSamples,
            TName testName,
            long memoryUsed);
}
