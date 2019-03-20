package com.fillumina.performance.time.sample.iterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParallelTest implements Runnable {

    public static interface ConcurrentRunnable {

        /**
         * Run the current thread.
         *
         * @param index of the worker within the same group.
         */
        public void run(int index);
    }

    static class Group {
        private final String name;
        private final int workers;
        private final ConcurrentRunnable concurrentRunnable;

        public Group(String name,
                int workers,
                ConcurrentRunnable concurrentRunnable) {
            this.name = name;
            this.workers = workers;
            this.concurrentRunnable = concurrentRunnable;
        }

        public String getName() {
            return name;
        }

        public int getWorkers() {
            return workers;
        }

        public ConcurrentRunnable getConcurrentRunnable() {
            return concurrentRunnable;
        }

        @Override
        public String toString() {
            return "Group{" + "name=" + name + ", workers=" + workers + '}';
        }
    }

    private final List<Group> runnableGroup = new ArrayList<>();
    private final List<Group> unmodifiableRunnableGroup =
            Collections.unmodifiableList(runnableGroup);

    /**
     * Adds <i>count</i> workers that will run the given <i>test</i>
     * concurrently. The workers will be allocated to active threads accordingly
     * to the specified configurations. In case of a single thread all the
     * workers will be executed consecutively and their time will be
     * accounted together;
     *
     * @param name  test name
     * @param count number of workers to add
     * @param test  run
     */
    public ParallelTest addTask(String name, int count, ConcurrentRunnable test) {
        runnableGroup.add(new Group(name, count, test));
        return this;
    }

    boolean isGroupPresent() {
        return !runnableGroup.isEmpty();
    }

    List<Group> getGroups() {
        return unmodifiableRunnableGroup;
    }

    /** Not supported, use {@link #addGroup(int,Runnable)} instead. */
    @Override
    public final void run() {
        throw new UnsupportedOperationException("must use " +
                ParallelMultiThreadPerformanceExecutor.class.getSimpleName() +
                " executor for asymmetric tests.");
    }
}
