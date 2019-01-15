package com.fillumina.performance.executor.stats;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsInfo {

    private final boolean hidden;
    private final Map<String, Object> parameters;
    private final Map<String, Object> sequences;

    public StatsInfo(boolean hidden, Map<String, Object> parameters,
            Map<String, Object> sequences) {
        this.hidden = hidden;
        this.parameters = parameters;
        this.sequences = sequences;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Map<String, Object> getSequences() {
        return sequences;
    }

}
