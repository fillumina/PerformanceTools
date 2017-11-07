package com.fillumina.performance.mock;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.stats.AbstractStatsProducer;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class does not impose its data but hands it if a specific test
 * name is requested.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsProducerMock
        extends AbstractStatsProducer<StatsProducerMock, StatsMock> {

    private final Map<TName, Double> map;
    private final List<List<TName>> executions = new ArrayList<>();
    private final LinkedTree<CharSequence,Object> tree = new LinkedTree<>();

    public StatsProducerMock(Object... objects) {
        map = new LinkedMap<>();
        for (int i=0,l=objects.length; i<l; i+=2) {
            CharSequence c = (CharSequence) objects[i];
            double value = Double.valueOf(objects[i+1].toString());
            map.put(TN.tname(c), value);
        }
    }

    public StatsProducerMock(Map<TName, Double> map) {
        this.map = map;
    }

    /** Override if you want to record stuff. */
    public Object evaluate(CharSequence testName, Runnable test) {
        return null;
    }

    @Override
    public MixedAssertableHolder get() {
        StatsMockBuilder builder = StatsMock.builder().name(getName());
        executions.add(getTests().keyList());
        final int evaluationCounter = tree.size();
        Tree<CharSequence,Object> subtree =
                tree.addTree("" + evaluationCounter, evaluationCounter);
        getTests().forEach((TName name, Runnable test) -> {
            TName cname = getName().append(name);
            subtree.put(cname, evaluate(cname, test));
            builder.addTest(cname)
                    .mean(map.get(cname))
                    .samples(33)
                    .stdev(0)
                    .endTest();
        });
        return builder.buildWithCoincidentalValues();
    }

    /**
     * The test requested for execution.
     */
    public List<List<TName>> getTestNamesPerExecution() {
        return executions;
    }

    /**
     * <ul>
     * <li>first tree branch is the stats producer executions counter
     * (calls to {@link #get()} or {@link #execute()}).
     * <li>second tree branch is the value of the parameters as recorded in
     * {@link #evaluate(CharSequence, Runnable) }.
     * </ul>
     */
    public LinkedTree<CharSequence, Object> getEvaluatedTree() {
        return tree;
    }
}
