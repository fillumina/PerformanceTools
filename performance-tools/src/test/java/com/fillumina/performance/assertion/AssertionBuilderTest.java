package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionBuilderTest {

    public static class AssertionBuilderImpl
            extends AssertionBuilder<AssertionBuilderImpl, AssertionBuilderImpl> {

        private List<ExperimentAssertion> list;

        public static AssertionBuilderImpl create() {
            List<ExperimentAssertion> list = new ArrayList<>();
            return new AssertionBuilderImpl(list, list::add);
        }

        public AssertionBuilderImpl(List<ExperimentAssertion> list,
                Consumer<ExperimentAssertion> assertionConsumer) {
            super(assertionConsumer);
            this.list = list;
        }

        public ExperimentAssertion getExperimentAssertion() {
            return list.get(0);
        }
    }

    @Test
    public void shouldAssertPercentage() {
        AssertionBuilderImpl builder = AssertionBuilderImpl.create();
        builder.assertPercentage("alpha").greaterThan(10);
        ExperimentAssertion ea = builder.getExperimentAssertion();
        assertTrue(ea instanceof PercentageAssertion);
    }

    @Test
    public void shouldAssertOrder() {
        AssertionBuilderImpl builder = AssertionBuilderImpl.create();
        builder.assertOrder("alpha").greaterThan("beta");
        ExperimentAssertion ea = builder.getExperimentAssertion();
        assertTrue(ea instanceof OrderAssertion);
    }

    @Test
    public void shouldAssertValue() {
        AssertionBuilderImpl builder = AssertionBuilderImpl.create();
        builder.assertValue("alpha").greaterThan(10);
        ExperimentAssertion ea = builder.getExperimentAssertion();
        assertTrue(ea instanceof ValueAssertion);
    }

    @Test
    public void shouldAccept() {
        AssertionBuilderImpl builder = AssertionBuilderImpl.create();
        builder.accept(new ExperimentAssertion() {
            @Override
            public void check(AssertableExperiment t) {
                //
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment obj)
                    throws IOException {
                //
            }
        });
    }

    @Test
    public void shouldSetTolerance() {
        AssertionBuilderImpl builder = AssertionBuilderImpl.create();
        final Ratio tolerance = Ratio.percentage(77.77);
        builder.setTolerance(tolerance);
        assertEquals(tolerance, builder.getTolerance());
    }

}
