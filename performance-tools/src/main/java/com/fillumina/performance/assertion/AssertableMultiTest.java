package com.fillumina.performance.assertion;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableMultiTest {

    Map<String, ? extends AssertableTest> getPerformances();
}
