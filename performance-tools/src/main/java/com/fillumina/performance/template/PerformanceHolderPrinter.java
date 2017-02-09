package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.infrastructure.PerformanceHolder;
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
 * @param ST    speed statistics tree
 * @param MT    memory statistics tree
 * @param SA    speed assertion
 * @param MA    memory assertion
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceHolderPrinter
        <ST,
        MT,
        SA extends Assertion<ST>,
        MA extends Assertion<MT>> {

    private final SA speedAssertions;
    private final MA usedMemoryAssertions;
    private final MA allocatedMemoryAssertions;
    private final PerformanceHolder<SpeedStats,ST> speedTree;
    private final PerformanceHolder<MemStats,MT> usedMemTree;
    private final PerformanceHolder<MemStats,MT> allocatedMemTree;

    public static <ST, MT, SA extends Assertion<ST>, MA extends Assertion<MT>>
                String print(MixedAssertion<SA, MA> assertion,
                        PerformanceHolder<SpeedStats,ST> speedStats,
                        PerformanceHolder<MemStats,MT> usedMemStats,
                        PerformanceHolder<MemStats,MT> allocatedMemStats) {
                    return new PerformanceHolderPrinter<>(assertion, speedStats, usedMemStats,
                            allocatedMemStats).toString();
                }

    public PerformanceHolderPrinter(
            MixedAssertion<SA, MA> assertion,
            PerformanceHolder<SpeedStats,ST> speedStats,
            PerformanceHolder<MemStats,MT> usedMemStats,
            PerformanceHolder<MemStats,MT> allocatedMemStats) {
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
            implements PerformanceHolder.Visitor<S> {
        private final StringBuilder buf = new StringBuilder();

        @Override
        @SuppressWarnings("unchecked")
        public String toString() {

            if (speedTree != null) {
                speedTree.traverse((PerformanceHolder.Visitor<SpeedStats>) this);
            } else if (usedMemTree != null) {
                usedMemTree.traverse((PerformanceHolder.Visitor<MemStats>) this);
            } else if (allocatedMemTree != null) {
                allocatedMemTree.traverse((PerformanceHolder.Visitor<MemStats>) this);
            }
            return buf.toString();
        }

        void println(String s) {
            buf.append(s).append(System.lineSeparator());
        }

        @Override
        public void visitTitle(int level, ComposedName name) {
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
                PerformanceHolder<A,T> tree,
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