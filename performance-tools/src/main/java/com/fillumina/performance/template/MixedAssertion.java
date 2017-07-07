package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
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

    public AssertionSelector<?, MixedAssertion<C>> speed() {
        return new AssertionSelector<>(this, assertion -> {
            addAvgTimeAssertion(assertion);
        }, tolerance);
    }

    public AssertionSelector<?, MixedAssertion<C>> usedMemory() {
        return new AssertionSelector<>(this, assertion -> {
            addUsedMemAssertion(assertion);
        }, tolerance);
    }

    public AssertionSelector<?, MixedAssertion<C>> allocatedMemory() {
        return new AssertionSelector<>(this, assertion -> {
            addAllocatedMemAssertion(assertion);
        }, tolerance);
    }

    public Parameterized parameterized() {
        return new Parameterized();
    }

    public class Parameterized {

        public Parameterized tolerance(Ratio tolerance) {
            MixedAssertion.this.tolerance = tolerance;
            return this;
        }

        public TNameMatcherAssertion.Builder<Parameterized> avgTime() {
            return TNameMatcherAssertion.builder(assertion -> {
                        addAvgTimeAssertion(assertion);
                        return this;
                    });
        }

        public TNameMatcherAssertion.Builder<Parameterized> throughput() {
            return TNameMatcherAssertion.builder(assertion -> {
                        addThroughputAssertion(assertion);
                        return this;
                    });
        }

        public TNameMatcherAssertion.Builder<Parameterized> usedMemory() {
            return TNameMatcherAssertion.builder(assertion -> {
                        addUsedMemAssertion(assertion);
                        return this;
                    });
        }

        public TNameMatcherAssertion.Builder<Parameterized> allocatedMemory() {
            return TNameMatcherAssertion.builder(assertion -> {
                        addAllocatedMemAssertion(assertion);
                        return this;
                    });
        }

        public MixedAssertion<C> end() {
            return MixedAssertion.this;
        }
    }

    private void addAvgTimeAssertion(Assertion assertion) {
        mixedStats.getStats(AverageTimeStats.class)
                .addAssertion(assertion);
    }

    private void addThroughputAssertion(Assertion assertion) {
        mixedStats.<ThroughputStats>getStats(ThroughputStats.class)
                .addAssertion(assertion);
    }

    private void addUsedMemAssertion(Assertion assertion) {
        mixedStats.<UsedMemStats>getStats(UsedMemStats.class)
                .addAssertion(assertion);
    }

    private void addAllocatedMemAssertion(Assertion assertion) {
        mixedStats.<AllocatedMemStats>getStats(AllocatedMemStats.class)
                .addAssertion(assertion);
    }
}
