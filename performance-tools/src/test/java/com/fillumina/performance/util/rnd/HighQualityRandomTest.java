package com.fillumina.performance.util.rnd;

import java.util.Random;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HighQualityRandomTest extends AbstractRandomTestHelper {

    @Override
    protected Random getRandom() {
        return new HighQualityRandom();
    }

}
