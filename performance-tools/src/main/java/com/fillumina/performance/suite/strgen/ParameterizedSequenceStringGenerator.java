package com.fillumina.performance.suite.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequenceStringGenerator<A extends Assertable>
    implements StringGenerator<PHolder<PHolder<A>>>, Serializable {
    private static final long serialVersionUID = 1L;

    private final StringGenerator<PHolder<A>> printer;

    public ParameterizedSequenceStringGenerator(
            StringGenerator<PHolder<A>> printer) {
        this.printer = printer;
    }

    @Override
    public String toString(PHolder<PHolder<PHolder<A>>> paramSeqStatsHolder) {
        if (paramSeqStatsHolder == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (PHolder<PHolder<A>> paramStatsHolder : paramSeqStatsHolder) {
            buf.append(printer.toString(paramStatsHolder));
        }
        return buf.toString();
    }

}
