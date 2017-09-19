package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertableMapWrapper implements Assertable {

    private final Map<String, Measure> map;

    public AssertableMapWrapper(Map<String, Measure> map) {
        this.map = map;
    }

    @Override
    public Set<String> getNames() {
        return map.keySet();
    }

    @Override
    public Measure getMeasure(CharSequence name) {
        return map.get(name.toString());
    }
}
