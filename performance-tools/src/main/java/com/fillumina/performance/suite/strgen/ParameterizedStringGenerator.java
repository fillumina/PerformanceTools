package com.fillumina.performance.suite.strgen;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedStringGenerator<A extends AssertableMultiStats>
    implements StringGenerator<A>, Serializable {
    private static final long serialVersionUID = 1L;

    private final StringGenerator<A> printer;

    public ParameterizedStringGenerator(StringGenerator<A> printer) {
        this.printer = printer;
    }

    @Override
    public String toString(PerformanceHolder<A> parameterizedStats) {
        if (parameterizedStats == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (PerformanceHolder<A> subPerf : parameterizedStats) {
            buf.append(printer.toString(subPerf));
        }
        return buf.toString();
    }
}
