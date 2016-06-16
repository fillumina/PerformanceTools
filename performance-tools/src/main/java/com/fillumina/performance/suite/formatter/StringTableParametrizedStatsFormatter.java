package com.fillumina.performance.suite.formatter;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.formatter.StringTableStatsFormatter;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.infrastructure.StringGenerator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableParametrizedStatsFormatter
    implements StringGenerator<Map<ComposedName, PerformanceStats>>,
        Serializable {

    private static final long serialVersionUID = 1L;

    public static final StringTableParametrizedStatsFormatter INSTANCE =
            new StringTableParametrizedStatsFormatter();

    public static final PerformanceViewer<Map<ComposedName, PerformanceStats>>
            VIEWER = new PerformanceViewer<>(INSTANCE);

    private StringTableParametrizedStatsFormatter() {}

    @Override
    public String toString(ComposedName name,
            Map<ComposedName, PerformanceStats> parametrizedStats) {
        return /*TableFormatter.title(name.toString(), '=') +*/
                toString(parametrizedStats);
    }

    @Override
    public String toString(Map<ComposedName, PerformanceStats> parametrizedStats) {
        if (parametrizedStats == null) {
            return null;
        }
        final StringTableStatsFormatter printer =
                StringTableStatsFormatter.INSTANCE;
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<ComposedName, PerformanceStats> entry :
                parametrizedStats.entrySet()) {
            ComposedName testName = entry.getKey();
            PerformanceStats stats = entry.getValue();
            buf.append(printer.toString(testName, stats))
                    .append(System.lineSeparator());
        }
        return buf.toString();
    }
}
