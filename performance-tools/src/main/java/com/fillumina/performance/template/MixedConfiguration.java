package com.fillumina.performance.template;

import com.fillumina.performance.util.tname.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MixedConfiguration extends AlertPlayer.Configuration {

    TName getTestName();

    TestConfiguration<?> getTestConfig();

    SpeedConfiguration<?> getSpeed();

    MemConfiguration<?> getUsedMem();

    MemConfiguration<?> getAllocatedMem();

    MixedAssertion<?> getAssertions();

    MixedStats.Builder getMixedStatsBuilder();

    boolean isThrowExceptionIfFailingAssertion();

    Appendable getOutput();

    TestListener getTestListener();
}
