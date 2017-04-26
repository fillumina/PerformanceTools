package com.fillumina.performance.mem;

import com.fillumina.performance.util.TreeName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemProgressionStatusListener {

    void accepts(TreeName fullTestName,
            int sample,
            int totalSamples,
            String testName,
            long memoryUsed);
}
