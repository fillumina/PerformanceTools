package com.fillumina.performance.suite.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import java.io.Serializable;
import com.fillumina.performance.infrastructure.type.AssertableParameterizedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedStringGenerator
        <A extends AssertableParameterizedStats & Assertable>
    implements StringGenerator<A>, Serializable {
    private static final long serialVersionUID = 1L;

    private final StringGenerator<A> printer;

    public ParameterizedStringGenerator(StringGenerator<A> printer) {
        this.printer = printer;
    }

    @Override
    public String toString(PHolder<A> parameterizedStats) {
        if (parameterizedStats == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (PHolder<A> subPerf : parameterizedStats) {
            buf.append(printer.toString(subPerf));
        }
        return buf.toString();
    }
}
