package com.fillumina.performance.suite.viewer;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableParametrizedStatsViewer
    implements PerformanceConsumer<Map<ComposedName, PerformanceStats>>,
        PerformanceFormatter<Map<ComposedName, PerformanceStats>>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final StringTableParametrizedStatsViewer INSTANCE =
            new StringTableParametrizedStatsViewer();

    private StringTableParametrizedStatsViewer() {}

    @Override
    public void consume(ComposedName name,
            Map<ComposedName, PerformanceStats> performances) {
        System.out.println(toString(name, performances));
    }

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
        final StringTableStatsViewer printer =
                StringTableStatsViewer.INSTANCE;
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<ComposedName, PerformanceStats> entry :
                parametrizedStats.entrySet()) {
            ComposedName testName = entry.getKey();
            PerformanceStats stats = entry.getValue();
            buf.append(printer.toString(testName, stats));
        }
        return buf.toString();
    }
}
