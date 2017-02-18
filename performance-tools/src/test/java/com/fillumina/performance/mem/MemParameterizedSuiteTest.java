package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.ParameterizedSequenceAssertion;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.mem.strgen.UsedMemStatsStringGenerator;
import com.fillumina.performance.suite.ParameterizedSequenceTestable;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.interval.IntegerInterval;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemParameterizedSuiteTest {
    private static final String PARAM = "param";

    private AppendableWrapper printout = new AppendableWrapper();

    public static void main(final String[] args) {
        final MemParameterizedSuiteTest test = new MemParameterizedSuiteTest();
        test.printout = new AppendableWrapper(System.out);
        test.shouldAccountParameters();
    }

    @Test
    public void shouldAccountParameters() {
        final ParameterizedSequenceAssertion
                <Assertion<MemStats>, MemStats> assertion =
                AssertMemory.parameterizedSequence();

        final Iterable<Integer> interval =
                IntegerInterval.from(0).to(20).step(5);

        for (int i : interval) {
            int expectedMem = 16 + i * 4;
            // memory is allocated padded to the next 8 bytes
            int paddedMem = (int)
                    MemoryAllocatorInfo.INSTANCE.alignWithPadding(expectedMem);
            printout.write(i).write(" -> ").write(paddedMem).newline();

            assertion.forSequenceValue(Integer.toString(i))
                    .forAllTests()
                        .withTolerance(0)
                            .assertValue(PARAM).sameAs(paddedMem);
        }

        UsedMemConsumptionExecutor.createMemAnalyzer()
            .instrumentedBy(MemSuite.<Void>parameterizedSuite())
                // even if param is not used (void) must be inserted as null
            .addParameter(PARAM, null)
            .addPerformanceConsumerIf(!printout.isNullAppendable(),
                    UsedMemStatsStringGenerator.parameterizedViewer())
            .instrumentedBy(MemSuite.<Void,Integer>parameterizedSequenceSuite())
            .setSequence(interval)
            .addPerformanceConsumerIf(!printout.isNullAppendable(),
                    UsedMemStatsStringGenerator.parameterizedSequenceViewer())
            .addTest("test", new ParameterizedSequenceTestable<Void,Integer>() {
                @Override
                public Object test(Void param, Integer sequence) {
                    return new int[sequence];
                }
            })
            .execute()
            .printTo(printout)
            .checkAndPrint(printout, assertion);
    }
}
