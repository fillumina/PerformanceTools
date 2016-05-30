package com.fillumina.performance.suite.viewer;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableParametrizedSequenceStatsViewer
    implements PerformanceConsumer
            <Map<ComposedName, Map<ComposedName, PerformanceStats>>>,
        PerformanceFormatter
            <Map<ComposedName, Map<ComposedName, PerformanceStats>>>,
        Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableParametrizedSequenceStatsViewer INSTANCE =
            new StringTableParametrizedSequenceStatsViewer();

    private StringTableParametrizedSequenceStatsViewer() {}

    @Override
    public void consume(ComposedName name,
            Map<ComposedName, Map<ComposedName, PerformanceStats>> performances) {
        System.out.println(toString(name, performances));
    }

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
        final StringTableParametrizedStatsViewer printer =
                StringTableParametrizedStatsViewer.INSTANCE;
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
