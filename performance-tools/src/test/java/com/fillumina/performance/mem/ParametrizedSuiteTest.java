package com.fillumina.performance.mem;

import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.util.interval.IntegerInterval;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedSuiteTest {

    @Test
    public void shouldAccountParameters() {
        new MemAnalyzer()
            .instrumentedBy(MemSuite.<Void>parametrizedSuite())
            .instrumentedBy(MemSuite.<Void,Integer>parametrizedSequenceSuite())
            .setSequence(IntegerInterval.from(0).to(50).step(5))
            .addTest(null, new ParametrizedSequenceTestable<Void,Integer>() {
                @Override
                public Object test(Void param, Integer sequence) {
                    return new int[sequence];
                }
            })
            .execute()
            .print();
    }
}
