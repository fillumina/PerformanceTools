package com.fillumina.performance.util.rnd;

import java.util.Random;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CachedRandomTest extends AbstractRandomTestHelper {

    @Override
    protected Random getRandom() {
        return new CachedRandom(1 << 16, new HighQualityRandom());
    }
}
