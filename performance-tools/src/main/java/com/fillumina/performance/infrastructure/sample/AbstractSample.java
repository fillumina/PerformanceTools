package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.infrastructure.stats.SingleStats;
import com.fillumina.performance.infrastructure.stats.Stats;
import com.fillumina.performance.infrastructure.stats.SampleCollector;
import com.fillumina.performance.util.tname.TNameMap;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSample<I extends AbstractSample<I,V,S,T>,
                    V extends SampleValue,
                    S extends Stats<T>,
                    T extends SingleStats>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TNameMap<V> map;

    public AbstractSample(TNameMap<V> map) {
        this.map = map;
    }

    public abstract SampleCollector<S, T, I, V> getSampleCollector();

    public V getTestSample(CharSequence name) {
        return map.get(name);
    }

    public Collection<? extends CharSequence> getTestNames() {
        return map.keyList();
    }

    public List<V> getTestSamples() {
        return Collections.unmodifiableList(map.values());
    }

    public double getValue(CharSequence testName) {
        V testSample = getTestSample(testName);
        if (testSample == null) {
            throw new TestNotFoundException(testName, getTestNames());
        }
        return testSample.getValue();
    }
}
