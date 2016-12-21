package com.fillumina.performance.mem;

import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.AbstractTestable;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryStructuresConsumptionTest {

    public static void main(final String[] args) {
        System.out.println("" + UsedMemConsumptionExecutor.INSTANCE
                .execute(new AbstractTestable(){

            @Override
            public Object test() {
                return new byte[1];
            }
        }));
    }

    @Test
    public void shouldByteArrayUsingASingleByte() {
        long bytes = UsedMemConsumptionExecutor.INSTANCE
                .execute(new AbstractTestable(){

            @Override
            public Object test() {
                return new byte[1]; // 16 + 1 + PADDING = 24
            }
        });

        assertEquals(24, bytes);
    }

    @Test
    public void shouldCharArrayUsingATwoBytes() {
        long bytes = UsedMemConsumptionExecutor.INSTANCE
                .execute(new AbstractTestable(){

            @Override
            public Object test() {
                return new char[12]; // 16 + (2 * 12) = 40
            }
        });

        assertEquals(40, bytes);
    }
}
