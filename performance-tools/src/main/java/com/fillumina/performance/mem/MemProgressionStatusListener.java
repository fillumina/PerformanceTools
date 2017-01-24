package com.fillumina.performance.mem;

import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemProgressionStatusListener {

    void accepts(ComposedName fullTestName,
            int sample,
            int totalSamples,
            String testName,
            long memoryUsed);
}
