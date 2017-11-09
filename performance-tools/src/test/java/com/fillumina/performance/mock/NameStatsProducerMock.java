package com.fillumina.performance.mock;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.stats.AbstractStatsProducer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NameStatsProducerMock extends AbstractStatsProducer<NameStatsProducerMock, StatsMock> {

    private List<List<CharSequence>> tree = new ArrayList<>();
    private int index = 1;

    @Override
    public MixedAssertableHolder get() {
        StatsMockBuilder builder = StatsMock.builder().name(getName());
        List<CharSequence> sample = new ArrayList<>(getTests().size());
        tree.add(sample);
        getTests().
                forEach((CharSequence name, Runnable test) -> {
            sample.add(name);
            builder.addTest(name).mean(index).samples(33).stdev(0).endTest();
            index++;
        });
        return builder.buildWithCoincidentalValues();
    }

    public List<List<CharSequence>> getTree() {
        return tree;
    }

}
