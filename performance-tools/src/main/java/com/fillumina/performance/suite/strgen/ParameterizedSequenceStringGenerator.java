package com.fillumina.performance.suite.strgen;

import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequenceStringGenerator<A>
    implements StringGenerator
            <Map<ComposedName, Map<ComposedName, A>>>,
        Serializable {
    private static final long serialVersionUID = 1L;

    private final StringGenerator<Map<ComposedName, A>> printer;

    public ParameterizedSequenceStringGenerator(
            StringGenerator<Map<ComposedName, A>> printer) {
        this.printer = printer;
    }

    @Override
    public String toString(ComposedName message,
            Map<ComposedName, Map<ComposedName, A>> parameterizedStats) {
        return /*TableFormatter.frame(message.toString(), '*') +*/
                toString(parameterizedStats);
    }

    @Override
    public String toString(
            Map<ComposedName, Map<ComposedName, A>> parameterizedStats) {
        if (parameterizedStats == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<ComposedName, Map<ComposedName, A>> entry :
                parameterizedStats.entrySet()) {
            ComposedName testName = entry.getKey();
            Map<ComposedName, A> map = entry.getValue();
            buf.append(printer.toString(testName, map));
        }
        return buf.toString();
    }

}
