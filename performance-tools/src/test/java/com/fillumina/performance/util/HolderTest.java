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

    @Test
    public void shouldIncrementAndGet() {
        Holder.Integer holder = new Holder.Integer(0);
        assertEquals(1, holder.incrementAndGet(), 0);
    }

    @Test
    public void shouldGetAndIncrement() {
        Holder.Integer holder = new Holder.Integer(0);
        assertEquals(0, holder.getAndIncrement(), 0);
        assertEquals(1, holder.getAndIncrement(), 0);
    }

    @Test
    public void shouldDecrementAndGet() {
        Holder.Integer holder = new Holder.Integer(10);
        assertEquals(9, holder.decrementAndGet(), 0);
    }

    @Test
    public void shouldGetAndDecrement() {
        Holder.Integer holder = new Holder.Integer(10);
        assertEquals(10, holder.getAndDecrement(), 0);
        assertEquals(9, holder.getAndDecrement(), 0);
    }
}
