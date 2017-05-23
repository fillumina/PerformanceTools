package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Prints speed, used mem and allocated mem results on a per-test basis
 * instead that one after the other.
 *
 * @param S    speed statistics (leaf)
 * @param M    memory statistics (leaf)
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedPrinter {

    private final StringGenerator<SpeedStats> speedStatsViewer;
    private final StringGenerator<MemStats> usedMemStatsViewer;
    private final StringGenerator<MemStats> allocMemStatsViewer;

    public MixedPrinter(
            StringGenerator<SpeedStats> speedStatsViewer,
            StringGenerator<MemStats> usedMemStatsViewer,
            StringGenerator<MemStats> allocMemStatsViewer) {
        this.speedStatsViewer = speedStatsViewer;
        this.usedMemStatsViewer = usedMemStatsViewer;
        this.allocMemStatsViewer = allocMemStatsViewer;
    }

    public String toString(
            MixedAssertion assertion,
            PHolder<SpeedStats> speedStats,
            PHolder<MemStats> usedMemStats,
            PHolder<MemStats> allocatedMemStats) {
        return new Appender(new StringBuilder())
                .append(assertion, speedStats, usedMemStats, allocatedMemStats)
                .toString();
    }

    public void appendTo(Appendable appendable,
            MixedAssertion assertion,
            PHolder<SpeedStats> speedStats,
            PHolder<MemStats> usedMemStats,
            PHolder<MemStats> allocatedMemStats) {
        new Appender(appendable)
                .append(assertion, speedStats, usedMemStats, allocatedMemStats);
    }

    private class Appender extends AppendableWrapper {

        public Appender(Appendable appendable) {
            super(appendable);
        }

        public Appendable append(
                MixedAssertion assertion,
                PHolder<SpeedStats> speedStats,
                PHolder<MemStats> usedMemStats,
                PHolder<MemStats> allocatedMemStats) {

            LinkedMap<TName,SpeedStats> speed = getMap(speedStats);
            LinkedMap<TName,MemStats> used = getMap(usedMemStats);
            LinkedMap<TName,MemStats> alloc = getMap(allocatedMemStats);

            List<TName> names = extractNames(speed, used, alloc);

            for (TName name : names) {
                appendTitle(name);

                appendExperiment(speedStatsViewer,
                        speed, assertion.getSpeedAssertions(), name);

                appendExperiment(usedMemStatsViewer,
                        used, assertion.getUsedMemoryAssertions(), name);

                appendExperiment(allocMemStatsViewer,
                        alloc, assertion.getAllocatedMemoryAssertions(), name);

            }

            return getAppendable();
        }

        private void appendTitle(TName title) {
            append(TableFormatter.title(title.toString(), '-'));
            newline();
        }

        private <T extends Assertable> LinkedMap<TName, T> getMap(PHolder<T> tree) {
            if (tree != null && !tree.isEmpty()) {
                return tree.getFlattenedAssertableMap();
            }
            return new LinkedMap<>();
        }

        @SafeVarargs
        private final List<TName> extractNames(LinkedMap<TName, ?>... trees) {
            for (LinkedMap<TName, ?> t : trees) {
                if (!t.isEmpty()) {
                    return new ArrayList<>(t.keySet());
                }
            }
            throw new AssertionError("unexpected error");
        }

        private <A extends Assertable> void appendExperiment(
                StringGenerator<A> viewer,
                LinkedMap<TName, A> map,
                StatsAssertion<?, A> assertion,
                TName n) {
            A assertable = map.get(n);
            if (assertable != null) {
                viewer.append(getAppendable(), assertable);
                newline();
            }
            if (assertion != null) {
                try {
                    assertion.check(assertable);
                    assertion.append(getAppendable(), assertable);
                } catch (AssertionError ex) {
                    append(ex.toString());
                }
                newline();
                newline();
                newline();
            }
        }
    }

}