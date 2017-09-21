package com.fillumina.performance.mem.stats;

import com.fillumina.performance.util.tname.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemProgressionStatusListener {

    void accepts(TName testName, int sample, int totalSamples, long memoryUsed);

}
