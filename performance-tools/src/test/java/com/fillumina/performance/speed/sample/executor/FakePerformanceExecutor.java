package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.speed.sample.executor.PerformanceExecutor;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.sample.Testable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FakePerformanceExecutor implements PerformanceExecutor {

    private final PerformanceSample sample;

    public FakePerformanceExecutor(PerformanceSample sample) {
        this.sample = sample;
    }

    @Override
    public PerformanceSample executeTests(Map<String, Testable> tests,
            int[] iterations) {
        return sample;
    }
}
