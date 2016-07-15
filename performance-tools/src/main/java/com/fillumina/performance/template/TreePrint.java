package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.StatsTree;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.SpeedStatsTableStringGenerator;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.util.Arrays;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TreePrint<S extends Assertion<?>, M extends Assertion<?>> {

    private final String title;
    private final S speedAssertions;
    private final M usedMemoryAssertions;
    private final M allocatedMemoryAssertions;
    private final StatsTree<SpeedStats> speedTree;
    private final StatsTree<MemStats> usedMemTree;
    private final StatsTree<MemStats> allocatedMemTree;

    public TreePrint(String title,
            MixedAssertion<S, M> assertion,
            StatsTree<SpeedStats> speedStats,
            StatsTree<MemStats> usedMemStats,
            StatsTree<MemStats> allocatedMemStats) {
        this.title = title;
        this.speedAssertions = assertion.getSpeedAssertions();
        this.usedMemoryAssertions = assertion.getUsedMemoryAssertions();
        this.allocatedMemoryAssertions = assertion.getAllocatedMemoryAssertions();
        this.speedTree = speedStats;
        this.usedMemTree = usedMemStats;
        this.allocatedMemTree = allocatedMemStats;
    }

    @Override
    public String toString() {
        return new VisitorImpl().toString();
    }

    private class VisitorImpl implements StatsTree.Visitor {
        private final StringBuilder buf = new StringBuilder();

        @Override
        public String toString() {
            println("");
            if (title != null) {
                println(TableFormatter.title("RESULTS FOR " + q(title), '='));
            } else {
                println(TableFormatter.title("RESULTS", '='));
            }

            StatsTree<? extends AssertableMultiStats> tree =
                    calculateNotNullTree();

            tree.traverse(this);
            return buf.toString();
        }

        void println(String s) {
            buf.append(s).append(System.lineSeparator());
        }

        StatsTree<? extends AssertableMultiStats> calculateNotNullTree() {
            for (StatsTree<? extends AssertableMultiStats> st :
                    Arrays.asList(speedTree, usedMemTree, allocatedMemTree)) {
                if (st != null) {
                    return st;
                }
            }
            throw new RuntimeException("no stats found!");
        }

        @Override
        public void visitTitle(int level, ComposedName name) {
        }

        private String q(String s) {
            return "'" + s + "'";
        }

        @Override
        public void visitStats(ComposedName name, AssertableMultiStats stats) {
            println(TableFormatter.title(name.toString(), '-'));
            printLeaf(speedTree, name,
                    SpeedStatsTableStringGenerator.INSTANCE,
                    speedAssertions);
            printLeaf(usedMemTree, name,
                    MemStatsTableStringGenerator.USED_INSTANCE,
                    usedMemoryAssertions);
            printLeaf(allocatedMemTree, name,
                    MemStatsTableStringGenerator.ALLOCATED_INSTANCE,
                    allocatedMemoryAssertions);
        }

        <A extends AssertableMultiStats> void printLeaf(
                StatsTree<A> tree,
                ComposedName name,
                StringGenerator<A> viewer,
                Assertion<?> assertion) {
            if (tree != null) {
                A ams = tree.getStats(name);
                if (ams != null) {
                    println(viewer.toString(null, ams));
                    if (assertion != null) {
                        for (Assertion<AssertableMultiStats> a :
                                assertion.getLeaves(name)) {
                            println(a.toString(ams));
                        }
                    }
                }
            }
        }
    }
}
