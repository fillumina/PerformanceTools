package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemUtil;
import com.fillumina.performance.mem.sample.MemoryEvaluatorInfo;
import com.fillumina.performance.util.MostUsedValueBag;
import java.util.Locale;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Check for the upper limit of mem evaluation accuracy.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HugeMemoryTest {

    public static void main(final String[] args) {
        evaluateHugeMemorySequentially();
//        doubleArraySize();
    }

    private static void evaluateHugeMemorySequentially() {
        int bytes = getMaxByteArrayAllocableSize();
        for (int i=0; i<100; i++) {
            System.out.println(MemoryEvaluatorInfo.INSTANCE.getDebugString());
            System.out.println(i + "\t=\t" +
                    allocatedMemoryForByteArrayOfSize(bytes) + "\n\n");
        }
    }

    private static void doubleArraySize() {
        System.out.println(MemoryEvaluatorInfo.INSTANCE.getDebugString());
        System.out.println("memory used by of an array of double of given size:");
        for (int i=1; i<22; i++) {
            int size = 1 << i;
            final long used = usedMemoryForByteArrayOfDoubleSize(size);
            final String str = String.format(Locale.US,
                    "i = %d \tsize = %,d \tresult = %,d \tdiff = %,d",
                    i, size, used, size - used);
            System.out.println(str);
        }
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
     * Use {@link MemoryEvaluatorInfo#calculateMaxDetectableMemory(java.lang.Appendable) }
     * to know which is the maximum memory correctly reported.
     */
    @Test
    public void shouldEvaluateABigObject() {
        // it seems that is a safe value
        final int size = getMaxByteArrayAllocableSize();
        final MemoryEvaluatorInfo info = MemoryEvaluatorInfo.INSTANCE;

        final String message = info.getDebugString();
        final int expected = size + info.getMinimalAllocableMemory();
        final int tolerance = 0;
        final long memUsed = MemAnalyzer.used(
                () -> SafeSink.drain(new byte[size]));

        assertEquals(message, expected, memUsed, tolerance);

//        System.out.println("shouldEvaluateABigObject:\n" + message);
    }

    private static int getMaxByteArrayAllocableSize() {
        long max = calculateMaxDetectableMemory();
        int min = MemoryEvaluatorInfo.INSTANCE.getMinimalAllocableMemory();
        int size = (int) MemUtil.alignUp( (max >> 1) - min, 8);
        System.out.println("MAX=" + max + ", MIN=" + min + ", SIZE=" + size);
        return size;
    }

    private static int calculateMaxDetectableMemory() {
        MostUsedValueBag<Long> maxBag = new MostUsedValueBag<>(10);
        for (int k=0; k<10; k++) {
            long max = MemoryEvaluatorInfo.INSTANCE.calculateMaxDetectableMemory(System.out);
            maxBag.add(max);
        }
        return maxBag.getMostUsedValue().intValue();
    }
}
