package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.Testable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CountingTestable extends Testable {
    private AtomicInteger counter = new AtomicInteger();

    @Override
    public void test() {
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }
}

