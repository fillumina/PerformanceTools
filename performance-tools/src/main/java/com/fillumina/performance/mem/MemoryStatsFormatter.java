package com.fillumina.performance.mem;

import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.MemoryUnit;
import com.fillumina.performance.util.TableFormatter;
import com.fillumina.performance.util.stats.Measure;
import java.util.Map;
import com.fillumina.performance.infrastructure.StringGenerator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryStatsFormatter
        implements StringGenerator<Map<String,Measure>> {

    @Override
    public String toString(Map<String, Measure> map) {
        TableFormatter tf = new TableFormatter();
        for (Map.Entry<String, Measure> entry : map.entrySet()) {
            String name = entry.getKey();
            Measure mem = entry.getValue();
            tf.cell(name).cell(MemoryUnit.prettyPrint(mem)).endl();
        }
        return tf.toString();
    }

    @Override
    public String toString(ComposedName name, Map<String, Measure> t) {
        return TableFormatter.title(name.toString(), '=') + toString(t);
    }
}
