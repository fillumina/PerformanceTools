package com.fillumina.performance.infrastructure;

import com.fillumina.performance.infrastructure.Drain;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SinkTest {

    @Test
    public void shouldDrainObject() {
        Drain.drain(this);
    }

    @Test
    public void shouldDrainBoolean() {
        Drain.drain(true);
    }

    @Test
    public void shouldDrainByte() {
        Drain.drain(Byte.MAX_VALUE);
    }

    @Test
    public void shouldDrainShort() {
        Drain.drain(Short.MAX_VALUE);
    }

    @Test
    public void shouldDrainChar() {
        Drain.drain(Character.MAX_VALUE);
    }

    @Test
    public void shouldDrainInt() {
        Drain.drain(Integer.MAX_VALUE);
    }

    @Test
    public void shouldDrainLong() {
        Drain.drain(Long.MAX_VALUE);
    }

    @Test
    public void shouldDrainFloat() {
        Drain.drain(Float.MAX_VALUE);
        Drain.drain(Float.POSITIVE_INFINITY);
    }

    @Test
    public void shouldDrainDouble() {
        Drain.drain(Double.MAX_VALUE);
        Drain.drain(Double.POSITIVE_INFINITY);
    }

}
