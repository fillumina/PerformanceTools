package com.fillumina.performance.mem;

import com.fillumina.performance.speed.sample.AbstractTestable;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryStructuresConsumptionTest {
    private static final int PADDING = 1;

    @Test
    public void shouldByteArrayUsingASingleByte() {
        long bytes = new UsedMemConsumptionExecutor()
                .execute(new AbstractTestable(){

            @Override
            public Object test() {
                return new byte[15]; // 16 + 15 + PADDING = 32
            }
        });

        assertEquals(16 + 15 + PADDING, bytes);
    }

    @Test
    public void shouldCharArrayUsingATwoBytes() {
        long bytes = new UsedMemConsumptionExecutor()
                .execute(new AbstractTestable(){

            @Override
            public Object test() {
                return new char[12]; // 16 + (2 * 12) + PADDING = 40
            }
        });

        assertEquals(40, bytes);
    }
}
