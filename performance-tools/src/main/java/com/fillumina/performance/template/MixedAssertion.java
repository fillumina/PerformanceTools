package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.AssertionSelector;
import com.fillumina.performance.assertion.MultiAssertion;
import com.fillumina.performance.assertion.MultiAssertionFactory;
import com.fillumina.performance.assertion.TNameMatcherAssertion;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertion<C> extends CallBackBuilder<C, MixedAssertion<C>> {

    private final List<Assertion<SpeedStats>> speedList = new ArrayList<>();
    private final List<Assertion<MemStats>> usedList = new ArrayList<>();
    private final List<Assertion<MemStats>> allocatedList = new ArrayList<>();
    private Ratio tolerance = Ratio.percentage(5);

    public MixedAssertion() {
        super();
    }

    public MixedAssertion(C caller) {
        super(caller);
    }

    public MixedAssertion(Setter<C, MixedAssertion<C>> setter) {
        super(setter);
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
        return new AssertionSelector<>(this, stats -> {
            speedList.add(stats);
        }, tolerance);
    }

    public AssertionSelector<?, MixedAssertion<C>, MemStats> usedMemory() {
        return new AssertionSelector<>(this, stats -> {
            usedList.add(stats);
        }, tolerance);
    }

    public AssertionSelector<?, MixedAssertion<C>, MemStats> allocatedMemory() {
        return new AssertionSelector<>(this, stats -> {
            allocatedList.add(stats);
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
            return new TNameMatcherAssertion<>((builtObject) -> {
                        speedList.add(builtObject);
                        return this;
                    });
        }

        public TNameMatcherAssertion<Parameterized, MemStats> usedMemory() {
            return new TNameMatcherAssertion<>((builtObject) -> {
                        usedList.add(builtObject);
                        return this;
                    });
        }

        public TNameMatcherAssertion<Parameterized, MemStats> allocatedMemory() {
            return new TNameMatcherAssertion<>((builtObject) -> {
                        allocatedList.add(builtObject);
                        return this;
                    });
        }

        public MixedAssertion<C> end() {
            return MixedAssertion.this;
        }
    }

    MultiAssertion<SpeedStats> getSpeedAssertions() {
        return MultiAssertionFactory.createFrom(speedList);
    }

    MultiAssertion<MemStats> getUsedMemoryAssertions() {
        return MultiAssertionFactory.createFrom(usedList);
    }

    MultiAssertion<MemStats> getAllocatedMemoryAssertions() {
        return MultiAssertionFactory.createFrom(allocatedList);
    }

}
