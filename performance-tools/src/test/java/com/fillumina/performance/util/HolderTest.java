package com.fillumina.performance.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HolderTest {
    private static final String HELLO__WORLD = "Hello World";

    @Test
    public void testGetValue() {
        final Holder<String> holder = new Holder<>();
        new Runnable() {
            @Override
            public void run() {
                holder.setValue(HELLO__WORLD);
            }

        }.run();

        assertEquals(HELLO__WORLD, holder.getValue());
    }

}
