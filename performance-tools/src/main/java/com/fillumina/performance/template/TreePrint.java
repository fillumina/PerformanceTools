package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.TableFormatter;

/**
 * Prints speed, used mem and allocated mem results on a per-test basis
 * instead that one after the other.
 *
 * @param ST    speed stats tree
 * @param MT    memory stats tree
 * @param SA    speed assertion
 * @param MA    memory assertion
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TreePrint
        <ST,
        MT,
        SA extends Assertion<ST>,
        MA extends Assertion<MT>> {

    private final SA speedAssertions;
    private final MA usedMemoryAssertions;
    private final MA allocatedMemoryAssertions;
    private final TreeHolder<SpeedStats,ST> speedTree;
    private final TreeHolder<MemStats,MT> usedMemTree;
    private final TreeHolder<MemStats,MT> allocatedMemTree;

    public static <ST, MT, SA extends Assertion<ST>, MA extends Assertion<MT>>
                String print(MixedAssertion<SA, MA> assertion,
                        TreeHolder<SpeedStats,ST> speedStats,
                        TreeHolder<MemStats,MT> usedMemStats,
                        TreeHolder<MemStats,MT> allocatedMemStats) {
                    return new TreePrint<>(assertion, speedStats, usedMemStats,
                            allocatedMemStats).toString();
                }

    public TreePrint(
            MixedAssertion<SA, MA> assertion,
            TreeHolder<SpeedStats,ST> speedStats,
            TreeHolder<MemStats,MT> usedMemStats,
            TreeHolder<MemStats,MT> allocatedMemStats) {
        this.speedAssertions = assertion.getSpeedAssertions();
        this.usedMemoryAssertions = assertion.getUsedMemoryAssertions();
        this.allocatedMemoryAssertions = assertion.getAllocatedMemoryAssertions();
        this.speedTree = speedStats;
        this.usedMemTree = usedMemStats;
        this.allocatedMemTree = allocatedMemStats;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        return new VisitorImpl<>().toString();
    }

    private class VisitorImpl<S extends AssertableMultiStats>
            implements TreeHolder.Visitor<S> {
        private final StringBuilder buf = new StringBuilder();

        @Override
        @SuppressWarnings("unchecked")
        public String toString() {

            if (speedTree != null) {
                speedTree.traverse((TreeHolder.Visitor<SpeedStats>) this);
            } else if (usedMemTree != null) {
                usedMemTree.traverse((TreeHolder.Visitor<MemStats>) this);
            } else if (allocatedMemTree != null) {
                allocatedMemTree.traverse((TreeHolder.Visitor<MemStats>) this);
            }
            return buf.toString();
        }

        void println(String s) {
            buf.append(s).append(System.lineSeparator());
        }

        @Override
        public void visitTitle(int level, ComposedName name) {
        }

        private String q(String s) {
            return "'" + s + "'";
        }

        @Override
        public void visitStats(ComposedName name, S stats) {
            if (name != null) {
                println(TableFormatter.title(name.toString(), '-'));
            }
            printLeaf(speedTree,
                    name,
                    WrapperSpeedStatsTableStringGenerator.INSTANCE,
                    speedAssertions);
            printLeaf(usedMemTree,
                    name,
                    MemStatsTableStringGenerator.USED_INSTANCE,
                    usedMemoryAssertions);
            printLeaf(allocatedMemTree,
                    name,
                    MemStatsTableStringGenerator.ALLOCATED_INSTANCE,
                    allocatedMemoryAssertions);
        }

        <A extends AssertableMultiStats, T> void printLeaf(
                TreeHolder<A,T> tree,
                ComposedName name,
                StringGenerator<A> viewer,
                Assertion<T> assertion) {
            if (tree != null) {
                T t = tree.getTree();
                if (t != null) {
                    A stats = tree.get(name);
                    if (stats != null) {
                        println(viewer.toString(stats));
                        if (assertion != null) {
                            println(assertion.toString(name, t));
                        }
                    }
                }
            }
        }
    }
}