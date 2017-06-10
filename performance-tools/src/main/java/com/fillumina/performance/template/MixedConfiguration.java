package com.fillumina.performance.template;

import com.fillumina.performance.util.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MixedConfiguration extends AlertPlayer.Configuration {

    // TODO add result confidence

    TName getTestName();

    TestConfiguration<?> getTestConfig();

    SpeedConfiguration<?> getSpeed();

    MemConfiguration<?> getUsedMem();

    MemConfiguration<?> getAllocatedMem();

    MixedAssertion<?> getAssertions();

    MixedStats getMixedStats();

    boolean isThrowExceptionIfFailingAssertion();

    Appendable getOutput();

    TestListener getTestListener();
}
