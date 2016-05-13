package com.fillumina.performance.suite.viewer;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.TableFormatter;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableParametrizedSequenceStatsViewer
    implements PerformanceConsumer<Map<String, Map<String, PerformanceStats>>>,
        PerformanceFormatter<Map<String, Map<String, PerformanceStats>>>,
        Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableParametrizedSequenceStatsViewer INSTANCE =
            new StringTableParametrizedSequenceStatsViewer();

    private StringTableParametrizedSequenceStatsViewer() {}

    @Override
    public void consume(String message,
            Map<String, Map<String, PerformanceStats>> performances) {
        System.out.println(toString(message, performances));
    }

    public String toString(String message,
            Map<String, Map<String, PerformanceStats>> parametrizedStats) {
        return TableFormatter.title(message, '-') + toString(parametrizedStats);
    }

    @Override
    public String toString(
            Map<String, Map<String, PerformanceStats>> parametrizedStats) {
        if (parametrizedStats == null) {
            return null;
        }
        final StringTableParametrizedStatsViewer printer =
                StringTableParametrizedStatsViewer.INSTANCE;
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, Map<String, PerformanceStats>> entry :
                parametrizedStats.entrySet()) {
            String testName = entry.getKey();
            Map<String, PerformanceStats> map = entry.getValue();
            buf.append(printer.toString(testName, map));
        }
        return buf.toString();
    }

}
