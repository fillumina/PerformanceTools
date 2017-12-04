package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.AssertionBuilder;
import com.fillumina.performance.executor.stats.TNameMatcherAssertion;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.mem.MemStatsType;
import com.fillumina.performance.time.TimeStatsType;
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

    public MixedAssertionBuilder(MixedAssertionableResult.Builder mixedStatsBuilder,
            C caller) {
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

    public MixedAssertionBuilder<C> addAssertion(
            StatsType type,
            Assertion assertion) {
        mixedStatsBuilder.getStatsBuilder(type).addAssertion(assertion);
        return this;
    }

    public AssertionBuilder<?, MixedAssertionBuilder<C>> addAssertionBuilder(
            StatsType type) {
        return new AssertionBuilder<>(this,
                a -> addAssertion(type, a),
                tolerance);
    }

    public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>>
        addAssertionMatcher(StatsType type) {
        return TNameMatcherAssertion.builder(assertion -> {
                    addAssertion(type, assertion);
                    return MixedAssertionBuilder.this;
                });
    }

    public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>> avgTime() {
        return addAssertionMatcher(TimeStatsType.AVERAGE);
    }

    public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>> throughput() {
        return addAssertionMatcher(TimeStatsType.THROUGHPUT);
    }

    public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>> usedMemory() {
        return addAssertionMatcher(MemStatsType.USED);
    }

    public TNameMatcherAssertion.Builder<MixedAssertionBuilder<C>> allocatedMemory() {
        return addAssertionMatcher(MemStatsType.ALLOCATED);
    }
}
