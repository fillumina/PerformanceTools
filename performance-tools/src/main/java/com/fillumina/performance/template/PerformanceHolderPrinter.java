package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PHolder.PerformanceVisitor;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.assertion.Assertable;

/**
 * Prints speed, used mem and allocated mem results on a per-test basis
 * instead that one after the other.
 *
 * @param S    speed
 * @param M    memory
 * @param SA    speed assertion
 * @param MA    memory assertion
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceHolderPrinter
        <S extends Assertable,
         M extends Assertable,
         SA extends Assertion<S>,
         MA extends Assertion<M>> {

    private final SA speedAssertions;
    private final MA usedMemoryAssertions;
    private final MA allocatedMemoryAssertions;
    private final PHolder<SpeedStats> speedTree;
    private final PHolder<MemStats> usedMemTree;
    private final PHolder<MemStats> allocatedMemTree;

    public static <S extends Assertable,
                   M extends Assertable,
                   SA extends Assertion<S>,
                   MA extends Assertion<M>>
                String print(MixedAssertion<SA, MA> assertion,
                        PHolder<SpeedStats> speedStats,
                        PHolder<MemStats> usedMemStats,
                        PHolder<MemStats> allocatedMemStats) {
                    return new PerformanceHolderPrinter<>(
                            assertion, speedStats, usedMemStats,
                            allocatedMemStats).toString();
                }

    public PerformanceHolderPrinter(
            MixedAssertion<SA, MA> assertion,
            PHolder<SpeedStats> speedStats,
            PHolder<MemStats> usedMemStats,
            PHolder<MemStats> allocatedMemStats) {
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
        return new PerformanceVisitorImpl<>().toString();
    }

    private class PerformanceVisitorImpl<S extends Assertable>
            implements PerformanceVisitor<S> {

        private final StringBuilder buf = new StringBuilder();

        @Override
        @SuppressWarnings("unchecked")
        public String toString() {

            if (speedTree != null) {
                speedTree.traverse((PerformanceVisitor<SpeedStats>) this);
            } else if (usedMemTree != null) {
                usedMemTree.traverse((PerformanceVisitor<MemStats>) this);
            } else if (allocatedMemTree != null) {
                allocatedMemTree.traverse((PerformanceVisitor<MemStats>) this);
            }
            return buf.toString();
        }

        void println(String s) {
            buf.append(s).append(System.lineSeparator());
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

        @SuppressWarnings("unchecked")
        <A extends Assertable> void printLeaf(
                PHolder<A> tree,
                ComposedName name,
                StringGenerator<A> viewer,
                Assertion<?> assertion) {
            if (tree != null) {
                PHolder<A> leaf = tree.getLeaf(name);
                if (leaf != null) {
                    println(viewer.toString(leaf));
                    if (assertion != null) {
                        println(((Assertion<A>)assertion).toString(leaf));
                    }
                }
            }
        }
    }
}