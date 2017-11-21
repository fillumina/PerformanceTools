package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.tname.TName;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsProgressionStatus {
    private final TName name;
    private final Collection<Stats> stats;
    private final String statusMessage;

    public StatsProgressionStatus(TName name,
            Collection<Stats> stats,
            String statusMessage) {
        this.name = name;
        this.stats = stats;
        this.statusMessage = statusMessage;
    }

    public TName getName() {
        return name;
    }

    public Collection<Stats> getStats() {
        return stats;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        TableFormatter table = new TableFormatter()
                        .headerLeft(name.toString(), '-')
                        .param("message", statusMessage);
        table.appendToCatchingIOException(buf);
        buf.append(System.lineSeparator());
        if (stats != null && !stats.isEmpty()) {
            for (Stats s : stats) {
                buf.append(stats.toString());
                buf.append(System.lineSeparator());
            }
        }
        return buf.toString();
    }
}
