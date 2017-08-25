package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MultiMeasure;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TimeStatsBuilder<T extends TimeStats> extends Builder<T> {

    static MultiMeasure createMultiMeasure(Measure global,
            LinkedHashMap<TName, SingleTimeStats> map) {
        Measure[] measures = extractMeasureArray(map.values());
        return new MultiMeasure(global, measures);
    }

    static Measure[] extractMeasureArray(Collection<SingleTimeStats> collection) {
        Measure[] measures = new Measure[collection.size()];
        int index = 0;
        for (SingleTimeStats tp : collection) {
            measures[index] = tp.getMeasure();
            index++;
        }
        return measures;
    }

    void add(TName name, int requiredSamples, List<IterationTime> samples);
}
