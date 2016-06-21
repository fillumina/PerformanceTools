package com.fillumina.performance.suite.strgen;

import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedSequenceStringGenerator<A>
    implements StringGenerator
            <Map<ComposedName, Map<ComposedName, A>>>,
        Serializable {
    private static final long serialVersionUID = 1L;

    private final StringGenerator<Map<ComposedName, A>> printer;

//    public static final ParametrizedSequenceStringGenerator INSTANCE =
//            new ParametrizedSequenceStringGenerator();
//
//    public static final PerformanceViewer
//            <Map<ComposedName, Map<ComposedName, PerformanceStats>>> VIEWER =
//            new PerformanceViewer<>(INSTANCE);

    public ParametrizedSequenceStringGenerator(
            StringGenerator<Map<ComposedName, A>> printer) {
        this.printer = printer;
    }

    @Override
    public String toString(ComposedName message,
            Map<ComposedName, Map<ComposedName, A>> parametrizedStats) {
        return /*TableFormatter.frame(message.toString(), '*') +*/
                toString(parametrizedStats);
    }

    @Override
    public String toString(
            Map<ComposedName, Map<ComposedName, A>> parametrizedStats) {
        if (parametrizedStats == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<ComposedName, Map<ComposedName, A>> entry :
                parametrizedStats.entrySet()) {
            ComposedName testName = entry.getKey();
            Map<ComposedName, A> map = entry.getValue();
            buf.append(printer.toString(testName, map));
        }
        return buf.toString();
    }

}
