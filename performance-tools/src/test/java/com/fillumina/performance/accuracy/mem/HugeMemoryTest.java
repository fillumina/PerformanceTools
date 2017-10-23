package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.sample.MemoryEvaluatorInfo;
import java.util.Locale;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Check for precision up to 1 << 18 = 262,144 which seems to be the last
 * value for which results are given with accuracy.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HugeMemoryTest {

    public static void main(final String[] args) {
        System.out.println("" + usedMemoryForByteArrayOfDoubleSize(0));
        System.out.println("" + usedMemoryForByteArrayOfDoubleSize(0));
        System.out.println("" + usedMemoryForByteArrayOfDoubleSize(0));
        System.out.println(MemoryEvaluatorInfo.INSTANCE.getDebugString());
        System.out.println("memory used by of an array of double of given size:");
        for (int i=1; i<22; i++) {
            int size = 1 << i;
            final long used = usedMemoryForByteArrayOfDoubleSize(size);
            final String str = String.format(Locale.US,
                    "i = %d \tsize = %,d \tresult = %,d \tdiff = %,d",
                    i, size, used, size - used);
//            System.out.println(measure.getLogMessages());

            System.out.println(str);
        }
//        for (int i=491_520; i<(1 << 20); i+=32_768) {
//            final int size = i;
//            final int used = (int) usedMemoryForByteArrayOfSize(size).getMean();
//            final String str = String.format(Locale.US,"i = %,d \tbytes = %,d \tdiff = %,d",
//                            size, used, size - used);
//            System.out.println(str);
//        }
    }

    @Test
    public void shouldEstimateAllocatedMemory() {
        final int bytes = 1 << 18; // 262,144
        assertEquals(16 + bytes, allocatedMemoryForByteArrayOfSize(bytes));
    }

    private static long allocatedMemoryForByteArrayOfSize(int size) {
        return MemAnalyzer.allocated(new Runnable() {
                    final Object[] array = new Object[1000];
                    int i = -1;

                    @Override
                    public void run() {
                        i++;
                        array[i] = new byte[size];
                        SafeSink.drain(array[i]);
                    }
                });
    }

    @Test
    public void shouldEstimateHighUsedMemory() {
        final int bytes = 1 << 18; // 262,144
        assertEquals(16 + bytes, usedMemoryForByteArrayOfSize(bytes));
    }

    private static long usedMemoryForByteArrayOfSize(final int size) {
        return MemAnalyzer.used(() -> SafeSink.drain(new byte[size]));
    }

    private static long usedMemoryForByteArrayOfDoubleSize(int size) {
        return MemAnalyzer.used(() -> {
                    byte[] a1 = new byte[size >> 1];
                    byte[] a2 = new byte[size >> 1];
                    SafeSink.drain(a1.length + a2.length);
                });
    }


    /**
     * The current memory estimator is not able to report accurately values
     * bigger than a certain amount. It depends on the accuracy of the
     * {@link Runtime#totalMemory() } method.
     * Use {@link MemoryEvaluatorInfo#calculateMemoryAccuracyThreshold(java.lang.Appendable) }
     * to know which is the maximum memory correctly reported.
     */
    @Test
    public void shouldEvaluateABigObject() {
        // it seems that is a safe value
        final int size = 1 << 17;

        final String message = MemoryEvaluatorInfo.INSTANCE.getDebugString();
        final int expected = size + 16;
        final int tolerance = 0;
        final long memUsed = MemAnalyzer.used(
                () -> SafeSink.drain(new byte[size]));

        assertEquals(message, expected, memUsed, tolerance);
    }
}
