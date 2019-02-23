package com.fillumina.performance.mem.stats;

import com.fillumina.performance.util.pathname.PathName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemProgressionStatusListener {

    void accepts(PathName testName, int sample, int totalSamples, long memoryUsed);
}
