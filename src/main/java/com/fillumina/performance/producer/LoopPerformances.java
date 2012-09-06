package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.timer.TestPerformances;
import com.fillumina.performance.util.Statistics;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author fra
 */
public class LoopPerformances implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long iterations;
    private final Map<String, TestPerformances> map;
    private final List<TestPerformances> list;
    private final Statistics stats;
    private final NameList nameList;
    private final ElapsedNanosecondsList elapsedNanosecondsList;
    private final NanosecondsPerCycleList nanosecondsPerCycleList;
    private final PercentageList percentageList;

    public LoopPerformances(final long iterations,
            final Map<String, Long> timeMap) {
        this.iterations = iterations;
        this.stats = createStatistics(timeMap);
        this.list = createList(timeMap);
        this.map = createMap(list);
        this.nameList = new NameList();
        this.elapsedNanosecondsList = new ElapsedNanosecondsList();
        this.nanosecondsPerCycleList = new NanosecondsPerCycleList();
        this.percentageList = new PercentageList();
    }

    private Statistics createStatistics(final Map<String, Long> timeMap) {
        final Collection<Long> values = timeMap.values();
        return new Statistics(values);
    }

    private List<TestPerformances> createList(
            final Map<String, Long> timeMap) {
        final List<TestPerformances> localList = new ArrayList<>(timeMap.size());
        final long fastest = Math.round(stats.max());

        for (Map.Entry<String, Long> entry: timeMap.entrySet()) {
            final String msg = entry.getKey();
            final Long elapsed = entry.getValue();

            final float percentage = elapsed * 100F / fastest;
            final double elapsedNanosecondsPerCycle = elapsed * 1.0D / iterations;

            final TestPerformances testPerformances = new TestPerformances(
                    msg, elapsed, percentage, elapsedNanosecondsPerCycle);

            localList.add(testPerformances);
        }

        return Collections.unmodifiableList(localList);
    }

    private Map<String, TestPerformances> createMap(
            final List<TestPerformances> list) {
        final Map<String, TestPerformances> localMap = new HashMap<>(list.size());
        for (final TestPerformances tp: list) {
            localMap.put(tp.getName(), tp);
        }
        return Collections.unmodifiableMap(localMap);
    }

    public int size() {
        return list.size();
    }

    public TestPerformances get(final String msg) {
        return map.get(msg);
    }

    public List<TestPerformances> getTests() {
        return list;
    }

    public long getIterations() {
        return iterations;
    }

    public Statistics getStatistics() {
        return stats;
    }

    public List<Long> getElapsedNanosecondsList() {
        return elapsedNanosecondsList;
    }

    public List<String> getNameList() {
        return nameList;
    }

    public List<Double> getNanosecondsPerCycleList() {
        return nanosecondsPerCycleList;
    }

    public List<Float> getPercentageList() {
        return percentageList;
    }

    public float getPercentageFor(final String name) {
        final TestPerformances test = get(name);
        if (test == null) {
            throw new IllegalStateException("Test \'" + name +
                    "\' does not exist!");
        }
        return test.getPercentage();
    }

    private abstract class AbstractInnerList<T> extends AbstractList<T>
            implements Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int size() {
            return list.size();
        }
    }

    private class NameList extends AbstractInnerList<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public String get(final int index) {
            return list.get(index).getName();
        }
    }

    private class ElapsedNanosecondsList extends AbstractInnerList<Long> {
        private static final long serialVersionUID = 1L;

        @Override
        public Long get(final int index) {
            return list.get(index).getElapsedNanoseconds();
        }
    }

    private class NanosecondsPerCycleList extends AbstractInnerList<Double> {
        private static final long serialVersionUID = 1L;

        @Override
        public Double get(int index) {
            return list.get(index).getElapsedNanosecondsPerCycle();
        }
    }

    private class PercentageList extends AbstractInnerList<Float> {
        private static final long serialVersionUID = 1L;

        @Override
        public Float get(int index) {
            return list.get(index).getPercentage();
        }
    }

    public String toString(final String message) {
        return message + ":\n" + toString();
    }

    @Override
    public String toString() {
        return new StringTableViewer(this)
                .getTable()
                .toString();
    }
}
