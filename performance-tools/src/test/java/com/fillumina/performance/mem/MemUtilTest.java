package com.fillumina.performance.mem;

import com.fillumina.performance.speed.sample.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.progression.RepeatingStatsProducerBuilder;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemUtilTest {

    @Test
    public void shouldAlignUp() {
        assertEquals(48, MemUtil.alignUp(40, 16));
    }

    @Test
    public void shouldAlignDown() {
        assertEquals(32, MemUtil.alignDown(40, 16));
    }

    @Test
    public void shouldAlignUpReturnValuesDivisibleByStep() {
        for (int i=0; i<128; i++) {
            long value = MemUtil.alignUp(i, 8);
            assertTrue(value % 8 == 0);
        }
    }

    @Test
    public void shouldAlignDownReturnValuesDivisibleByStep() {
        for (int i=0; i<128; i++) {
            long value = MemUtil.alignDown(i, 8);
            assertTrue(value % 8 == 0);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptStepNotPowerOf2() {
        MemUtil.alignUp(3, 7);
    }

    @Test
    public void shouldDetectPowerOf2() {
        assertTrue(MemUtil.isPowerOfTwo(0));
        for (int i=1; i<63; i++) {
            final long value = 1L << i;
            assertTrue("i=" + i + ", value=" + value,
                    MemUtil.isPowerOfTwo(value));
        }
    }

    @Test
    public void should1NotBePowerOf2() {
        assertFalse(MemUtil.isPowerOfTwo(1));
    }

    @Test
    public void shouldDetectPowerOf2OfAlternativeAlgorithm() {
        assertTrue(isPowerOfTwoAlternative(0));
        for (int i=1; i<63; i++) {
            final long value = 1L << i;
            assertTrue("i=" + i + ", value=" + value,
                    isPowerOfTwoAlternative(value));
        }
    }

    @Test
    public void shouldDetectNumbersNotPowerOf2() {
        for (int i=0; i<(1 << 16); i++) {
            assertEquals("value = " + i,
                    isPowerOfTwoAlternative(i),
                    MemUtil.isPowerOfTwo(i));
        }
    }

    /** Alternative algorithm, same speed. */
    private static boolean isPowerOfTwoAlternative(long x) {
        if (x < 0 || x == 1) {
            return false;
        }
        if (x == 0) {
            return true;
        }
        boolean flag = false;
        for (int i=0; i<64; i++) {
            long mask = 1L << i;
            if ((x & mask) != 0) {
                if (flag) {
                    return false;
                }
                flag = true;
            }
        }
        return flag;
    }

    @Test
    public void shouldReport() {
        assertTrue(MemUtil.isPowerOfTwo(1 << 14));
        assertTrue(isPowerOfTwoAlternative(1 << 14));
    }

    public static void main(final String[] args) {

        PerformanceTimerFactory.createSingleThreaded()
                .addPerformanceConsumer(SampleLineStringGenerator.VIEWER)
                .instrumentedBy(RepeatingStatsProducerBuilder.instance()
                            .setMaxPercentageMargin(3)
                            .build())
                .addTest("powerOf2", new Runnable() {
                    private int i;

                    @Override
                    public void run() {
                        Sink.drain(MemUtil.isPowerOfTwo(i++));
                    }
                })
                .addTest("alternative", new Runnable() {
                    private int i;

                    @Override
                    public void run() {
                        Sink.drain(isPowerOfTwoAlternative(i++));
                    }
                })
                .addPerformanceConsumer(WrapperSpeedStatsTableStringGenerator.VIEWER)

                .execute()
                .print();

    }

}
