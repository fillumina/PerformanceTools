package com.fillumina.performance.mock;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.stats.AbstractStatsProducer;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.Magnitude;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * This class does not impose its data but hands it if a specific test
 * name is requested.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsProducerMock<T>
        extends AbstractStatsProducer<StatsProducerMock<T>> {

    private final Map<TName, Double> map;
    private final List<Map<CharSequence,T>> tree = new ArrayList<>();
    private BiFunction<CharSequence,Runnable,T> evaluator = (x,y) -> null;

    public StatsProducerMock(Object... objects) {
        map = new IndexedHashMap<>();
        for (int i=0,l=objects.length; i<l; i+=2) {
            CharSequence c = (CharSequence) objects[i];
            double value = Double.valueOf(objects[i+1].toString());
            map.put(TN.tname(c), value);
        }
    }

    public StatsProducerMock(Map<TName, Double> map) {
        this.map = map;
    }

    public StatsProducerMock<T> evaluator(
            final BiFunction<CharSequence,Runnable,T> value) {
        this.evaluator = value;
        return this;
    }

    /** Override if you want to record stuff. */
    public T evaluate(CharSequence testName, Runnable test) {
        return evaluator.apply(testName, test);
    }

    @Override
    public MixedStatsHolder get() {
        StatsMockBuilder builder = new StatsMockBuilder().name(getName());
        Map<CharSequence,T> subTree = new IndexedHashMap<>();
        tree.add(subTree);
        getTests().forEach((TName name, Runnable test) -> {
            //TName cname = getName().append(name);
            subTree.put(name, evaluate(name, test));
            double testValue;
            try {
                testValue = map.get(name);
            } catch (NullPointerException e) {
                throw new RuntimeException("test not found: " + name, e);
            }
            builder.addTest(name)
                    .mean(testValue)
                    .samples(33)
                    .stdev(0)
                    .endTest();
        });
        return builder.buildWithCoincidentalValues(Magnitude.UNIT);
    }

    /**
     * <ul>
     * <li>first tree branch is the stats producer executions counter
     * (calls to {@link #get()} or {@link #execute()}).
     * <li>second tree branch is the value of the parameters as recorded in
     * {@link #evaluate(CharSequence, Runnable) }.
     * </ul>
     */
    public List<Map<CharSequence, T>> getEvaluatedTree() {
        return tree;
    }

}
