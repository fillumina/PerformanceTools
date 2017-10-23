package com.fillumina.performance.mem.sample;

/**
 * Uses {@link MemAnalyzerTest} for more reliable tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryConsumptionTest {

    public static void main(final String[] args) {
        System.out.println(MemoryEvaluatorInfo.INSTANCE.getDebugString());
        for (int i=0; i<4; i++) {
            MemoryConsumption mc = new MemoryConsumption();
            System.out.println(mc.toString());
        }
    }
}
