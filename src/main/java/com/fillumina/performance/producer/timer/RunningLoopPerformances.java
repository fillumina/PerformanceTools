package com.fillumina.performance.producer.timer;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author fra
 */
public class RunningLoopPerformances implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Long> timeMap = new LinkedHashMap<>();
    private final long iterations;

    public RunningLoopPerformances(final long iterations) {
        this.iterations = iterations;
    }

    public long getIterations() {
        return iterations;
    }

    public void add(final String msg, final long elapsed) {
        final Long time = timeMap.get(msg);
        final long value;
        if (time == null) {
            value = elapsed;
        } else {
            value = time + elapsed;
        }
        timeMap.put(msg, value);
    }

    public LoopPerformances getLoopPerformances() {
        return new LoopPerformances(iterations, timeMap);
    }
}
