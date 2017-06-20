package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.AssertionSelector;
import com.fillumina.performance.assertion.TNameMatcherAssertion;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertion<C> extends CallBackBuilder<C, MixedAssertion<C>> {
    public static final String SPEED = "SPEED";
    public static final String USED_MEM = "USED_MEM";
    public static final String ALLOCATED_MEM = "ALLOCATED_MEM";

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

    public AssertionSelector<?, MixedAssertion<C>, SpeedStats> speed() {
        return new AssertionSelector<>(this, assertion -> {
            addSpeedAssertion(assertion);
        }, tolerance);
    }

    public AssertionSelector<?, MixedAssertion<C>, MemStats> usedMemory() {
        return new AssertionSelector<>(this, assertion -> {
            addUsedMemAssertion(assertion);
        }, tolerance);
    }

    public AssertionSelector<?, MixedAssertion<C>, MemStats> allocatedMemory() {
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

        public TNameMatcherAssertion<Parameterized, SpeedStats> speed() {
            return new TNameMatcherAssertion<>(assertion -> {
                        addSpeedAssertion(assertion);
                        return this;
                    });
        }

        public TNameMatcherAssertion<Parameterized, MemStats> usedMemory() {
            return new TNameMatcherAssertion<>(assertion -> {
                        addUsedMemAssertion(assertion);
                        return this;
                    });
        }

        public TNameMatcherAssertion<Parameterized, MemStats> allocatedMemory() {
            return new TNameMatcherAssertion<>(assertion -> {
                        addAllocatedMemAssertion(assertion);
                        return this;
                    });
        }

        public MixedAssertion<C> end() {
            return MixedAssertion.this;
        }
    }

    private void addSpeedAssertion(Assertion<SpeedStats> assertion) {
        mixedStats.<SpeedStats>getStats(SPEED).addAssertion(assertion);
    }

    private void addUsedMemAssertion(Assertion<MemStats> assertion) {
        mixedStats.<MemStats>getStats(USED_MEM).addAssertion(assertion);
    }

    private void addAllocatedMemAssertion(Assertion<MemStats> assertion) {
        mixedStats.<MemStats>getStats(ALLOCATED_MEM).addAssertion(assertion);
    }
}
