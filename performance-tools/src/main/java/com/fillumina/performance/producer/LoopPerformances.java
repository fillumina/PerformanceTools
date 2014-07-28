package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.util.Statistics;
import java.io.Serializable;
import java.util.*;

/**
 * Takes the raw tests execution time and number of iterations performed
 * and elaborates some useful statistics.
 * The returned lists follow the ordering given by {@code timeMap.values()}
 * so if ordering is important be sure to use a
 * {@link LinkedHashMap} to pass tests' times.
 * This class is final and cannot be modified by clients.
 *
 * @author Francesco Illuminati
 */
public class LoopPerformances implements Serializable {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public static final LoopPerformances EMPTY = new LoopPerformances();

    private final long iterations;
    private final Map<String, TestPerformances> map;
    private final List<TestPerformances> list;
    private final Statistics stats;
    private final NameList nameList;
    private final ElapsedNanosecondsList elapsedNanosecondsList;
    private final NanosecondsPerCycleList nanosecondsPerCycleList;
    private final PercentageList percentageList;

    @SuppressWarnings("unchecked")
    private LoopPerformances() {
        this.iterations = 0;
        this.stats = Statistics.EMPTY;
        this.list = (List<TestPerformances>) Collections.EMPTY_LIST;
        this.map = (Map<String, TestPerformances>) Collections.EMPTY_MAP;
        this.nameList = new NameList();
        this.elapsedNanosecondsList = new ElapsedNanosecondsList();
        this.nanosecondsPerCycleList = new NanosecondsPerCycleList();
        this.percentageList = new PercentageList();
    }

    /**
     * Compute performance statistics based on iterations and a map
     * of tests and time elapsed.
     *
     * @param iterations number of iterations performed (it's used to extract
     *                      the time per cycle).
     * @param timeMap   a map between tests and number of nanoseconds elapsed
     *                  executing them. If the order is important use a
     *                  {@link LinkedHashMap}.
     */
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
            final String name = entry.getKey();
            final Long elapsed = entry.getValue();

            final float percentage = elapsed * 100F / fastest;
            final double elapsedNanosecondsPerCycle = elapsed * 1.0D / iterations;

            final TestPerformances testPerformances = new TestPerformances(
                    name, elapsed, percentage, elapsedNanosecondsPerCycle);

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

    public int numberOfTests() {
        return list.size();
    }

    /** Get {@link TestPerformances} by test name. */
    public TestPerformances getPerformancesFor(final String msg) {
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
        final TestPerformances test = getPerformancesFor(name);
        if (test == null) {
            throw new IllegalStateException("Test \'" + name +
                    "\' does not exist!");
        }
        return test.getPercentage();
    }

    /** This list is unmodifiable because of {@link AbstractList}. */
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
        return StringTableViewer.INSTANCE.getTable(this).toString();
    }
}
