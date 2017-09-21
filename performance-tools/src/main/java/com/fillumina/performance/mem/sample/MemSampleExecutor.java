package com.fillumina.performance.mem.sample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemSampleExecutor {

    long execute(Runnable runnable);

    long execute(int repetitions, Runnable runnable);
}
