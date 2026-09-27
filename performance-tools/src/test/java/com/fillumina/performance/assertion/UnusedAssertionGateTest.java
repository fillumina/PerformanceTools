package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.StatsMockBuilder;
import java.io.IOException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class UnusedAssertionGateTest {
    @Test
    public void shouldFailWhenAnAssertionMatchesNoMeasure() {
        AssertionReport report = new AssertionReport();
        report.setUnused(ExperimentAssertion.OK);

        assertFalse(report.getUnused().getUnusedAssertionList().isEmpty());
        assertFalse("an unchecked gate cannot succeed", report.isAllSuccessful());
    }

    @Test
    public void shouldRejectAnAssertionThatLooksUpAMissingMeasure() {
        AssertionReport report = new AssertionReport();
        ExperimentAssertion missing = new ExperimentAssertion() {
            @Override
            public void check(AssertableExperiment experiment) {
                experiment.getMeasure("not-present");
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment experiment)
                    throws IOException {
                appendable.append("not-present");
            }
        };
        report.add(missing, StatsMockBuilder.create("present", 1.0));

        assertFalse("a missing measure must fail the gate", report.isAllSuccessful());
    }

    @Test
    public void shouldPassWhenAnAssertionWasChecked() {
        AssertionReport report = new AssertionReport();
        report.setUnused(ExperimentAssertion.OK);
        report.setUsed(ExperimentAssertion.OK);
        assertTrue(report.isAllSuccessful());
    }
}
