package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.MemUtil;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Uses {@link MemAnalyzerTest} for more reliable tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryConsumptionTest {

    public static void main(final String[] args) {
        System.out.println(MemoryAllocatorInfo.INSTANCE.getDebugString());
//        for (int i=0; i<1; i++) {
//            MemoryConsumption mc = new MemoryConsumption();
//            System.out.println(mc.toString());
//        }
    }
    @Test
    public void shouldArmonizeZero() {
        assertEquals(0, MemUtil.alignDown(0, 16));
        assertEquals(32, MemUtil.alignDown(32, 16));
        assertEquals(32, MemUtil.alignDown(40, 16));
        assertEquals(48, MemUtil.alignDown(48, 16));
    }
}
