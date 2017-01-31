package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertParameterizedSequencePerformanceImpl;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.suite.ParameterizedSequenceTestable;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.interval.IntegerInterval;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemParameterizedSuiteTest {
    private Appendable printout;

    public static void main(final String[] args) {
        final MemParameterizedSuiteTest test = new MemParameterizedSuiteTest();
        test.printout = System.out;
        test.shouldAccountParameters();
    }

    @Test
    public void shouldAccountParameters() {
        final AssertParameterizedSequencePerformanceImpl<Void, MemStats> ps =
                AssertMemory.parameterizedSequence();
        for (int i=0; i<=50; i+=5) {
            int expectedMem = 16 + i * 4;
            // memory is allocated padded to the next 8 bytes
            int paddedMem = (int) (Math.ceil(expectedMem / 8.0) * 8);
            print("" + i + " -> " + paddedMem);

            ps.forSequenceValue(Integer.toString(i))
                    .forAllTests(AssertMemory.withTolerance(0)
                                    .assertValue("param").sameAs(paddedMem))
                    .endTests();
        }

        Map<ComposedName, Map<ComposedName, MemStats>> stats =
                UsedMemConsumptionExecutor.createMemAnalyzer()
            .instrumentedBy(MemSuite.<Void>parameterizedSuite())
            .addParameter("param", null)
            .instrumentedBy(MemSuite.<Void,Integer>parameterizedSequenceSuite())
            .setSequence(IntegerInterval.from(0).to(20).step(5))
            .addTest("test", new ParameterizedSequenceTestable<Void,Integer>() {
                @Override
                public Object test(Void param, Integer sequence) {
                    return new int[sequence];
                }
            })
            .execute()
            .print(printout)
            .check(ps)
            .getTree();

        print(ps.toString(stats));
    }

    private void print(final String s) {
        if (printout != null) {
            try {
                printout.append(s);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
