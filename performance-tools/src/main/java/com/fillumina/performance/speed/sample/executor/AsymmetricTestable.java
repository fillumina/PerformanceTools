package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AsymmetricTestable extends Testable {

    public static class Group {
        private final String name;
        private final int workers;
        private final Runnable runnable;

        public Group(String name, int workers, Runnable runnable) {
            this.name = name;
            this.workers = workers;
            this.runnable = runnable;
        }

        public String getName() {
            return name;
        }

        public int getWorkers() {
            return workers;
        }

        public Runnable getRunnable() {
            return runnable;
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
     * @param count number of workers to add
     * @param test  test
     */
    public AsymmetricTestable addGroup(String name, int count, Runnable test) {
        runnableGroup.add(new Group(name, count, test));
        return this;
    }

    public boolean isGroupPresent() {
        return !runnableGroup.isEmpty();
    }

    public List<Group> getGroups() {
        return unmodifiableRunnableGroup;
    }

    /** Not supported, use {@link #addGroup(int,Runnable)} instead. */
    @Override
    public final void test() {
        for (Group group : runnableGroup) {
            for (int i=0; i<group.workers; i++) {
                group.runnable.run();
            }
        }
    }
}
