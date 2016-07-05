package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.interval.IntegerInterval;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemParametrizedSuiteTest {
    private boolean printout;

    public static void main(final String[] args) {
        final MemParametrizedSuiteTest test = new MemParametrizedSuiteTest();
        test.printout = true;
        test.shouldAccountParameters();
    }

    @Test
    public void shouldAccountParameters() {
        final AssertParametrizedSequencePerformance<Void, MemStats> ps =
                AssertMemory.parametrizedSequence();
        print("MEM = " + MemoryConsumption.INSTANCE.toString());
        for (int i=0; i<=50; i+=5) {
            int expectedMem = 16 + i * 4;
            // memory is allocated padded to the next 8 bytes
            //TODO add this to code
            int paddedMem = (int) (Math.ceil(expectedMem / 8.0) * 8);
            print("" + i + " -> " + paddedMem);

            ps.forSequence(Integer.toString(i))
                    .forAllTests(AssertMemory.withTolerance(0)
                                    .assertValue("param").sameAs(paddedMem))
                    .endTests();
        }

        Map<ComposedName, Map<ComposedName, MemStats>> stats = new MemAnalyzer()
            .instrumentedBy(MemSuite.<Void>parametrizedSuite())
            .addParameter("param", null)
            .instrumentedBy(MemSuite.<Void,Integer>parametrizedSequenceSuite())
            .setSequence(IntegerInterval.from(0).to(20).step(5))
            .addTest("test", new ParametrizedSequenceTestable<Void,Integer>() {
                @Override
                public Object test(Void param, Integer sequence) {
                    return new int[sequence];
                }
            })
            .execute()
            .printIf(printout)
            .check(ps)
            .getPerformance();

        print(ps.toString(stats));
    }

    private void print(final String s) {
        if (printout) {
            System.out.println(s);
        }
    }
}
