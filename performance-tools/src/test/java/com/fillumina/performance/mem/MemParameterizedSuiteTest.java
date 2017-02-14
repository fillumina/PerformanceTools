package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertParameterizedSequencePerformanceImpl;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.type.AssertableStats;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.mem.strgen.UsedMemStatsStringGenerator;
import com.fillumina.performance.suite.ParameterizedSequenceTestable;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.instrument.Instrumentable;
import com.fillumina.performance.util.instrument.Instrumenter;
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
        final AssertParameterizedSequencePerformanceImpl
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

    private static class BInstrumenter
            implements Instrumenter<StatsProducer<? extends AssertableStats>> {

        @Override
        public Instrumenter<StatsProducer<? extends AssertableStats>> instrument(
                StatsProducer<? extends AssertableStats> instrumentable) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private static class AInstrumenter
            implements Instrumenter<StatsProducer<AssertableStats>> {

        @Override
        public Instrumenter<StatsProducer<AssertableStats>> instrument(
                StatsProducer<AssertableStats> instrumentable) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private static class CInstrumenter<A extends AssertableStats>
            implements Instrumenter<StatsProducer<A>> {

        @Override
        public Instrumenter<StatsProducer<A>> instrument(
                StatsProducer<A> instrumentable) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private static class AInstrumentable<A extends AssertableStats>
            implements Instrumentable<StatsProducer<A>> {

        @Override
        public <T extends Instrumenter<StatsProducer<A>>> T instrumentedBy(
                T instrumenter) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private static class BInstrumentable<A extends AssertableStats>
            implements Instrumentable<BInstrumentable<A>> {

        @Override
        public <T extends Instrumenter<BInstrumentable<A>>> T instrumentedBy(
                T instrumenter) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    //FIXME
    private void blae() {
        final CInstrumenter<MemStats> instr = new CInstrumenter<>();
        final CInstrumenter<MemStats> ainst = instr;
        new BInstrumentable<>().instrumentedBy(instr);
    }

}
