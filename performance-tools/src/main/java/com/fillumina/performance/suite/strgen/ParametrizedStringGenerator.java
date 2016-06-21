package com.fillumina.performance.suite.strgen;

import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedStringGenerator<A>
    implements StringGenerator<Map<ComposedName, A>>,
        Serializable {
    private static final long serialVersionUID = 1L;

    private final StringGenerator<A> printer;

    public ParametrizedStringGenerator(StringGenerator<A> printer) {
        this.printer = printer;
    }

    @Override
    public String toString(ComposedName name,
            Map<ComposedName, A> parametrizedStats) {
        return /*TableFormatter.title(name.toString(), '=') +*/
                toString(parametrizedStats);
    }

    @Override
    public String toString(Map<ComposedName, A> parametrizedStats) {
        if (parametrizedStats == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<ComposedName, A> entry : parametrizedStats.entrySet()) {
            ComposedName testName = entry.getKey();
            A stats = entry.getValue();
            buf.append(printer.toString(testName, stats));
        }
        return buf.toString();
    }
}
