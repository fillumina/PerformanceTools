package com.fillumina.performance.suite.formatter;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.infrastructure.StringFormatter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableParametrizedSequenceStatsFormatter
    implements StringFormatter
            <Map<ComposedName, Map<ComposedName, PerformanceStats>>>,
        Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableParametrizedSequenceStatsFormatter INSTANCE =
            new StringTableParametrizedSequenceStatsFormatter();

    public static final PerformanceViewer
            <Map<ComposedName, Map<ComposedName, PerformanceStats>>> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    private StringTableParametrizedSequenceStatsFormatter() {}

    @Override
    public String toString(ComposedName message,
            Map<ComposedName, Map<ComposedName, PerformanceStats>> parametrizedStats) {
        return /*TableFormatter.frame(message.toString(), '*') +*/
                toString(parametrizedStats);
    }

    @Override
    public String toString(
            Map<ComposedName, Map<ComposedName, PerformanceStats>> parametrizedStats) {
        if (parametrizedStats == null) {
            return null;
        }
        final StringTableParametrizedStatsFormatter printer =
                StringTableParametrizedStatsFormatter.INSTANCE;
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<ComposedName, Map<ComposedName, PerformanceStats>> entry :
                parametrizedStats.entrySet()) {
            ComposedName testName = entry.getKey();
            Map<ComposedName, PerformanceStats> map = entry.getValue();
            buf.append(printer.toString(testName, map))
                    .append(System.lineSeparator());
        }
        return buf.toString();
    }

}
