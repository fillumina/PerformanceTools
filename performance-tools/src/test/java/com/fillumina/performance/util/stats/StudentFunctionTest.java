package com.fillumina.performance.util.stats;

import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.template.PerformanceBuilder;
import com.fillumina.performance.util.ExpBinarySearcher;
import com.fillumina.performance.util.sequence.IntegerSequence;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StudentFunctionTest {

    private static final int MAX = 100;

    public static void main(final String[] args) {
        PerformanceBuilder
                .config()
                    .speedConfig()
                        .setWarmupSamples(1)
                    .end()
                    .tests()
                        .addTest("original",
                                () -> SafeSink.drain(
                                        StatFunctions.student(0.999, 1E6)))
                        .addTest("cached",
                                () -> SafeSink.drain(
                                        StudentFunction.student(0.999, 1E6)))
                    .end()
                .end()
                .executeWithFullOutput();
    }

    /** This test takes 1.8 sec to perform. */
    @Ignore @Test
    public void slowStudent() {
        assertEquals(2.575853061335843, StatFunctions.student(0.99,2.2916802E7), 0);
        assertEquals(2.575853061335843, StudentFunction.student(0.99,2.2916802E7), 0);
    }

    @Ignore @Test
    public void assertFirstNAsymptote() {
        IntegerSequence.from(1).until(MAX).step(1).toList().parallelStream()
                .forEach((Integer i) -> {
            double p = i/(1.0 * MAX);
            int firstN = StudentFunction.FIRST_N[i];
            double before = StatFunctions.student(p, firstN - 1);
            double after = StatFunctions.student(p, firstN);
            double asymptote = StudentFunction.ASYMPTOTE[i];
            assertFalse("p=" + p, before == after);
            assertEquals("p=" + p, asymptote, after, 0);
            System.out.println("before=" + before + ",\tafter=" + after +
                    ",\tasymptote=" + asymptote);
        });
    }

    @Ignore @Test
    public void assertAsymptoteForBiggerNumbers() {
        IntegerSequence.from(0).until(MAX).step(1).toList().parallelStream()
                .forEach((Integer i) -> {
            double p = i/(1.0 * MAX);
            int firstN = StudentFunction.FIRST_N[i];
            double n = firstN * 1E3;
            double student = StatFunctions.student(p, n);
            assertEquals("p=" + p + ",\tn=" + n,
                    StudentFunction.ASYMPTOTE[i], student, 0);
            System.out.println("p=" + p + ",\tn=" + n + ",\tstudent=" + student);
        });
    }

    /**
     * Try to find values at which student function stabilize around the same
     * result for various values of p
     *
     * @param args
     */
    public static void createArrays(final String[] args) {
        createArrayOfAsymptote(MAX);

        System.out.println("");
        createArrayFirstNForAsymptote(MAX);
    }

    private static void createArrayOfAsymptote(int max) {
        for (int i=888; i<max; i++) {
            double p = i/(1.0 * max);
            System.out.println(StatFunctions.student(p,1E8) + ",\t// p=" + p);
        }
    }

    private static void createArrayFirstNForAsymptote(int max) {
        for (int i=0; i<max; i++) {
            final double p = i/(1.0 * max);
            final int idx = i;
            int first = ExpBinarySearcher.searchGreaterOrEquals(
                    1, Integer.MAX_VALUE,
                    (int v) -> StatFunctions.student(p, 1.0 * v) ==
                            StudentFunction.ASYMPTOTE[idx]);

            System.out.println(first + ",\t // p=" + p);
        }
    }
}
