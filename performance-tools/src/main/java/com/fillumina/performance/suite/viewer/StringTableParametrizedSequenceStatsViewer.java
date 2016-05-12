package com.fillumina.performance.suite.viewer;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.StringOutputHolder;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableParametrizedSequenceStatsViewer
    implements PerformanceConsumer<Map<String, Map<String, PerformanceStats>>>,
        Serializable {

    private static final long serialVersionUID = 1L;

    public static final StringTableParametrizedSequenceStatsViewer INSTANCE =
            new StringTableParametrizedSequenceStatsViewer();

    private StringTableParametrizedSequenceStatsViewer() {}

    @Override
    public void consume(String message,
            Map<String, Map<String, PerformanceStats>> performances) {
        getTable(message, performances).print();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated and there is no title.
     */
    public static StringOutputHolder toStringOutput(
            Map<String, Map<String, PerformanceStats>> parametrizedStats) {
        return getTable(null, parametrizedStats);
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    public static StringOutputHolder getTable(final String message,
            Map<String, Map<String, PerformanceStats>> parametrizedStats) {
        if (parametrizedStats == null) {
            return StringOutputHolder.NULL;
        }
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, Map<String, PerformanceStats>> entry :
                parametrizedStats.entrySet()) {
            String testName = entry.getKey();
            Map<String, PerformanceStats> map = entry.getValue();
            buf.append(StringTableParametrizedStatsViewer.getTable(testName, map));
        }
        return new StringOutputHolder(buf.toString());
    }

}
