package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.AssertionBuilder;
import com.fillumina.performance.assertion.TNameMatcherAssertion;
import com.fillumina.performance.executor.generator.MixedAssertionableResult;
import com.fillumina.performance.mem.stats.AllocatedMemStats;
import com.fillumina.performance.mem.stats.UsedMemStats;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertionBuilder<C>
        extends CallBackBuilder<C, MixedAssertionBuilder<C>> {

    private final MixedAssertionableResult.Builder mixedStatsBuilder;
    private Ratio tolerance = Ratio.percentage(5);

    public MixedAssertionBuilder(MixedAssertionableResult.Builder mixedStatsBuilder) {
        super();
        this.mixedStatsBuilder = mixedStatsBuilder;
    }

    public MixedAssertionBuilder(MixedAssertionableResult.Builder mixedStatsBuilder, C caller) {
        super(caller);
        this.mixedStatsBuilder = mixedStatsBuilder;
    }

    public MixedAssertionBuilder(MixedAssertionableResult.Builder mixedStatsBuilder,
            Setter<C, MixedAssertionBuilder<C>> setter) {
        super(setter);
        this.mixedStatsBuilder = mixedStatsBuilder;
    }

    public MixedAssertionBuilder<C> tolerance(Ratio tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    @Override
    public MixedAssertionBuilder<C> build() {
        return this;
    }

    public AssertionBuilder<?, MixedAssertionBuilder<C>> addAssertion(
            Class<? extends Assertable> type) {
        return new AssertionBuilder<>(this,
                a -> mixedStatsBuilder.getStatsBuilder(type).addAssertion(a),
                tolerance);
    }

    public AssertionBuilder<?, MixedAssertionBuilder<C>> avgTime() {
        return addAssertion(AverageTimeStats.class);
    }

    public AssertionBuilder<?, MixedAssertionBuilder<C>> throughput() {
        return addAssertion(ThroughputStats.class);
    }

    public AssertionBuilder<?, MixedAssertionBuilder<C>> usedMemory() {
        return addAssertion(UsedMemStats.class);
    }

    public AssertionBuilder<?, MixedAssertionBuilder<C>> allocatedMemory() {
        return addAssertion(AllocatedMemStats.class);
    }

    public Parameterized parameterized() {
        return new Parameterized();
    }

    public class Parameterized {

        public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>> addAssertion(
                Class<? extends Assertable> type) {
            return TNameMatcherAssertion.builder(assertion -> {
                        mixedStatsBuilder.getStatsBuilder(type)
                                .addAssertion(assertion);
                        return MixedAssertionBuilder.this;
                    });
        }

        public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>> avgTime() {
            return addAssertion(AverageTimeStats.class);
        }

        public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>> throughput() {
            return addAssertion(ThroughputStats.class);
        }

        public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>> usedMemory() {
            return addAssertion(UsedMemStats.class);
        }

        public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>> allocatedMemory() {
            return addAssertion(AllocatedMemStats.class);
        }

        public MixedAssertionBuilder<C> end() {
            return MixedAssertionBuilder.this;
        }
    }
}
