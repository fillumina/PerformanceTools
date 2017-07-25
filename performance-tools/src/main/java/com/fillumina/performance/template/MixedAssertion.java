package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.AssertionSelector;
import com.fillumina.performance.assertion.TNameMatcherAssertion;
import com.fillumina.performance.mem.AllocatedMemStats;
import com.fillumina.performance.mem.UsedMemStats;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertion<C> extends CallBackBuilder<C, MixedAssertion<C>> {

    private final MixedStats<?> mixedStats;
    private Ratio tolerance = Ratio.percentage(5);

    public MixedAssertion(MixedStats<?> mixedStats) {
        super();
        this.mixedStats = mixedStats;
    }

    public MixedAssertion(MixedStats<?> mixedStats, C caller) {
        super(caller);
        this.mixedStats = mixedStats;
    }

    public MixedAssertion(MixedStats<?> mixedStats,
            Setter<C, MixedAssertion<C>> setter) {
        super(setter);
        this.mixedStats = mixedStats;
    }

    public MixedAssertion<C> tolerance(Ratio tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    @Override
    public MixedAssertion<C> build() {
        return this;
    }

    public AssertionSelector<?, MixedAssertion<C>> addAssertion(
            Class<? extends Assertable> type) {
        return new AssertionSelector<>(this, assertion -> {
            mixedStats.getWritableStats(type).addAssertion(assertion);
        }, tolerance);
    }

    public AssertionSelector<?, MixedAssertion<C>> avgTime() {
        return addAssertion(AverageTimeStats.class);
    }

    public AssertionSelector<?, MixedAssertion<C>> throughput() {
        return addAssertion(ThroughputStats.class);
    }

    public AssertionSelector<?, MixedAssertion<C>> usedMemory() {
        return addAssertion(UsedMemStats.class);
    }

    public AssertionSelector<?, MixedAssertion<C>> allocatedMemory() {
        return addAssertion(AllocatedMemStats.class);
    }

    public Parameterized parameterized() {
        return new Parameterized();
    }

    public class Parameterized {

        public TNameMatcherAssertion.Builder<MixedAssertion<C>> addAssertion(
                Class<? extends Assertable> type) {
            return TNameMatcherAssertion.builder(assertion -> {
                        mixedStats.getWritableStats(type)
                                .addAssertion(assertion);
                        return MixedAssertion.this;
                    });
        }

        public TNameMatcherAssertion.Builder<MixedAssertion<C>> avgTime() {
            return addAssertion(AverageTimeStats.class);
        }

        public TNameMatcherAssertion.Builder<MixedAssertion<C>> throughput() {
            return addAssertion(ThroughputStats.class);
        }

        public TNameMatcherAssertion.Builder<MixedAssertion<C>> usedMemory() {
            return addAssertion(UsedMemStats.class);
        }

        public TNameMatcherAssertion.Builder<MixedAssertion<C>> allocatedMemory() {
            return addAssertion(AllocatedMemStats.class);
        }

        public MixedAssertion<C> end() {
            return MixedAssertion.this;
        }
    }
}
