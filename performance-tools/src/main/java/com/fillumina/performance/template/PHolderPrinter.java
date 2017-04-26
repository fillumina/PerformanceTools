package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PHolder.LeafVisitor;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.TreeName;
import com.fillumina.performance.util.formatter.TableFormatter;

/**
 * Prints speed, used mem and allocated mem results on a per-test basis
 * instead that one after the other.
 *
 * @param SL    speed statistics (leaf)
 * @param ML    memory statistics (leaf)
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PHolderPrinter<SL extends Assertable, ML extends Assertable> {

    private final StringGenerator<SL> speedStatsViewer;
    private final StringGenerator<ML> memStatsViewer;

    public PHolderPrinter(
            StringGenerator<SL> speedStatsViewer,
            StringGenerator<ML> memStatsViewer) {
        this.speedStatsViewer = speedStatsViewer;
        this.memStatsViewer = memStatsViewer;
    }

    public <S extends Assertable,
            M extends Assertable,
            SA extends Assertion<S>,
            MA extends Assertion<M>>
                String toString(MixedAssertion<SA, MA> assertion,
                                PHolder<S> speedStats,
                                PHolder<M> usedMemStats,
                                PHolder<M> allocatedMemStats) {
        return new PerformanceVisitorImpl<>(
                    assertion, speedStats, usedMemStats, allocatedMemStats)
                .toString();
    }

    private class PerformanceVisitorImpl<T extends Assertable,
                                         S extends Assertable,
                                         M extends Assertable,
                                         SA extends Assertion<S>,
                                         MA extends Assertion<M>>
            implements LeafVisitor<T> {

        private final SA speedAssertions;
        private final MA usedMemoryAssertions;
        private final MA allocatedMemoryAssertions;
        private final PHolder<S> speedTree;
        private final PHolder<M> usedMemTree;
        private final PHolder<M> allocatedMemTree;

        private final StringBuilder buf = new StringBuilder();

        public PerformanceVisitorImpl(
                MixedAssertion<SA, MA> assertion,
                PHolder<S> speedTree,
                PHolder<M> usedMemTree,
                PHolder<M> allocatedMemTree) {
            this.speedAssertions = assertion.getSpeedAssertions();
            this.usedMemoryAssertions = assertion.getUsedMemoryAssertions();
            this.allocatedMemoryAssertions = assertion.getAllocatedMemoryAssertions();
            this.speedTree = speedTree;
            this.usedMemTree = usedMemTree;
            this.allocatedMemTree = allocatedMemTree;
        }

        @Override
        @SuppressWarnings("unchecked")
        public String toString() {

            if (speedTree != null) {
                speedTree.traverseLeaves((LeafVisitor<S>) this);
            } else if (usedMemTree != null) {
                usedMemTree.traverseLeaves((LeafVisitor<M>) this);
            } else if (allocatedMemTree != null) {
                allocatedMemTree.traverseLeaves((LeafVisitor<M>) this);
            }
            return buf.toString();
        }

        void println(String s) {
            buf.append(s).append(System.lineSeparator());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void visitLeaf(TreeName name, T stats) {
            if (name != null && !name.isEmpty()) {
                println(TableFormatter.title(name.toString(), '-'));
            }
            printLeaf("Speed",
                    name,
                    (PHolder<T>) speedTree,
                    (StringGenerator<T>) speedStatsViewer,
                    (Assertion<T>) speedAssertions);
            printLeaf("Used Memory",
                    name,
                    (PHolder<T>) usedMemTree,
                    (StringGenerator<T>) memStatsViewer,
                    (Assertion<T>) usedMemoryAssertions);
            printLeaf("Allocated Memory",
                    name,
                    (PHolder<T>) allocatedMemTree,
                    (StringGenerator<T>) memStatsViewer,
                    (Assertion<T>) allocatedMemoryAssertions);
        }

        void printLeaf(String type,
                TreeName name,
                PHolder<T> tree,
                StringGenerator<T> viewer,
                Assertion<T> assertion) {
            if (tree != null) {
                PHolder<T> leaf = tree.getLeaf(name);
                if (leaf != null) {
                    println(type + ":");
                    println(viewer.toString(leaf));
                    if (assertion != null) {
                        println(assertion.toString(leaf));
                    }
                }
            }
        }
    }
}