package com.fillumina.performance.util.rnd;

import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractRandomTestHelper {

    protected abstract Random getRandom();

    @Test
    public void shouldBeAllNumbersEquallyProbable() {
        Random rnd = getRandom();
        int[] array = new int[128];
        final int totalCycles = 1 << 16;
        for (int i=0; i<totalCycles; i++) {
            array[rnd.nextInt(128)]++;
        }
        int expectedValue = totalCycles / array.length;
        int maxDifference = (int)(expectedValue * 0.20);
        for (int i=0; i<array.length; i++) {
            assertRange("" + Arrays.toString(array),
                    expectedValue, array[i], maxDifference);
        }
    }

    protected void assertRange(String msg,
            int expected, int result, int difference) {
        assertTrue(msg + ", expected=" + expected + ", result=" + result,
                Math.abs(expected - result) < difference);
    }

    @Test
    public void shouldNotBeSequences() {
        int maxSequence = maxSequence(getRandom(), 128);
        Assert.assertTrue("max sequence = " + maxSequence, maxSequence < 4);
    }

    public static int maxSequence(Random rnd, int n) {
        int maxSequence = 0;
        int seqLength = 0;
        int last, current = -1;
        final int totalCycles = 1 << 16;
        for (int i=0; i<totalCycles; i++) {
            last = current;
            current = rnd.nextInt(n);
            if (current == last) {
                seqLength++;
            } else {
                if (maxSequence < seqLength) {
                    maxSequence = seqLength;
//                    System.out.println(
//                            "value = " + current + ", seq = " + maxSequence);
                }
                seqLength = 0;
            }
            if (maxSequence < seqLength) {
                maxSequence = seqLength;
//                    System.out.println(
//                            "value = " + current + ", seq = " + maxSequence);
            }
        }
        return maxSequence;
    }
}
