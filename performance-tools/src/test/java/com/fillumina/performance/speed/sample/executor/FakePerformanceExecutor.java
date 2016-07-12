package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.speed.sample.executor.PerformanceExecutor;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.sample.Testable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FakePerformanceExecutor implements PerformanceExecutor {

    private final SpeedSample sample;

    public FakePerformanceExecutor(SpeedSample sample) {
        this.sample = sample;
    }

    @Override
    public SpeedSample executeTests(Map<String, Testable> tests,
            int[] iterations) {
        return sample;
    }
}
