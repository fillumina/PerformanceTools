package com.fillumina.performance.suite.strgen;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequenceStringGenerator<A extends AssertableMultiStats>
    implements StringGenerator<A>,
        Serializable {
    private static final long serialVersionUID = 1L;

    private final StringGenerator<A> printer;

    public ParameterizedSequenceStringGenerator(StringGenerator<A> printer) {
        this.printer = printer;
    }

    @Override
    public String toString(PerformanceHolder<A> paramSeqStatsHolder) {
        if (paramSeqStatsHolder == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (PerformanceHolder<A> paramStatsHolder : paramSeqStatsHolder) {
            buf.append(printer.toString(paramStatsHolder));
        }
        return buf.toString();
    }

}
