package com.fillumina.performance.mock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CountingTestable implements Runnable {
    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public void run() {
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }
}

