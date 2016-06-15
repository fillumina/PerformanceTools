package com.fillumina.performance.mem;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryConsumption {
    public static final MemoryAssessment ASSESSMENT = new MemoryAssessment();
    private final Runtime rt;
    private long zero = 0;
    private Object[] filler;
    private long usedMemoryBefore;
    private int start;

    public MemoryConsumption() {
        rt = Runtime.getRuntime();
        start();
        zero = getUsedMemory();
    }

    public final void start() {
        filler = new Object[ASSESSMENT.maxElementsInArray];
        System.gc();
        start = reachFirstThreshold(filler, 0);
        usedMemoryBefore = rt.totalMemory() - rt.freeMemory();
    }

    private static int reachFirstThreshold(Object[] filler, int start) {
        Runtime rt = Runtime.getRuntime();
        long before = rt.totalMemory() - rt.freeMemory();
        long after;
        for (int i=start; i<filler.length; i++) {
            filler[i] = new int[0];
            after = rt.totalMemory() - rt.freeMemory() - before;
            if (after > 0) {
                //System.out.println("k="+idx+"\tmem="+after);
                return i;
            }
        }
        return start;
    }

    long after;
    int idx;
    public final long getUsedMemory() {
        for (idx=start; idx<filler.length; idx++) {
            filler[idx] = new int[0];
            after = rt.totalMemory() - rt.freeMemory() - usedMemoryBefore;
            if (after > 0) {
                return after - ((idx - start) * ASSESSMENT.minGranularityByte) - zero;
            }
        }
        throw new AssertionError("memory assessment failed");
    }

    public static class MemoryAssessment {
        public final int minGranularityByte;
        private final int maxElementsInArray;

        public MemoryAssessment() {
            // cycle to give it another chance if it is not working
            for (int j=0; j<10; j++) {
                System.gc();
                Object[] filler = new Object[8491416];
                Runtime rt = Runtime.getRuntime();
                int k = reachFirstThreshold(filler, 0);

                long before = rt.totalMemory() - rt.freeMemory();
                long after;
                for (int i=k; i<filler.length; i++) {
                    filler[i] = new int[0]; // 16 bytes
                    after = rt.totalMemory() - rt.freeMemory() - before;
                    if (after > 0) {
                        this.minGranularityByte = (int)(after * 1.0 / (i-k));
                        this.maxElementsInArray = i-k;
                        return;
                    }
                }
            }
            throw new AssertionError("memory assessment failed");
        }
    }
}
