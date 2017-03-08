package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.CName;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractAssertionErrorTest {

    private static class AbstractAssertionErrorImpl
            extends AbstractAssertionError {
        private static final long serialVersionUID = 1L;
        private Measure actualMeasure;
        private double expected;

        public AbstractAssertionErrorImpl(StaticPath title,
                OrderCondition condition, Ratio tolerance) {
            super(title, condition, tolerance);
        }

        public void setActualMeasure(Measure actualValue) {
            this.actualMeasure = actualValue;
        }

        public void setExpected(double expected) {
            this.expected = expected;
        }

        @Override
        protected boolean isConditionSatisfied(OrderCondition condition,
                Ratio tolerance) {
            ConfidenceInterval interval =
                    actualMeasure.getConfidenceInterval(Ratio.P_99);
            double lower = interval.getLowerBound();
            double upper = interval.getUpperBound();
            ToleranceEvaluator.Value expectedValue =
                    new ToleranceEvaluator(tolerance).value(expected);
            switch (condition) {
                case EQUALS:
                    return expectedValue.between(lower, upper);
                case GREATER:
                    return expectedValue.lessThan(lower);
                case LESS:
                    return expectedValue.greaterThan(upper);
            }
            throw new AssertionError("not managed condition: " + condition);
        }

    }

    @Test
    public void shouldValidateWhatIfAlgorithmWhenEquals() {
        AbstractAssertionErrorImpl test = new AbstractAssertionErrorImpl(
                CName.EMPTY.append("test"),
                OrderCondition.EQUALS,
                Ratio.percentage(10));

        test.setActualMeasure(new OnlineMeasure(34));
        test.setExpected(34);

        Map<OrderCondition, ToleranceRequired> map = test.getWhatIfToleranceMap();
        assertEquals(0.01, map.get(OrderCondition.GREATER).getDecimal(), 0);
        assertEquals(0.01, map.get(OrderCondition.LESS).getDecimal(), 0);

        StringBuilder buf = new StringBuilder();
        test.appendWhatIfTolerance(buf);
        assertEquals(
                "Would have been:" +
                "----------------" +
                "less if tolerance >= 1.000 %" +
                "greater if tolerance >= 1.000 %",
            buf.toString().replaceAll(System.lineSeparator(), ""));
    }

    @Test
    public void shouldValidateWhatIfAlgorithmWhenLessThan() {
        AbstractAssertionErrorImpl test = new AbstractAssertionErrorImpl(
                CName.EMPTY.append("test"),
                OrderCondition.EQUALS,
                Ratio.percentage(10));

        test.setActualMeasure(new OnlineMeasure(34));
        test.setExpected(28);

        Map<OrderCondition, ToleranceRequired> map = test.getWhatIfToleranceMap();
        assertEquals(0.22, map.get(OrderCondition.EQUALS).getDecimal(), 0);
        assertEquals(0.22, map.get(OrderCondition.LESS).getDecimal(), 0);

        StringBuilder buf = new StringBuilder();
        test.appendWhatIfTolerance(buf);
        assertEquals(
                "Would have been:" +
                "----------------" +
                "equals if tolerance >= 22.000 %" +
                "less if tolerance >= 22.000 %",
            buf.toString().replaceAll(System.lineSeparator(), ""));
    }

    @Test
    public void shouldValidateWhatIfAlgorithmWhenGreaterThan() {
        AbstractAssertionErrorImpl test = new AbstractAssertionErrorImpl(
                CName.EMPTY.append("test"),
                OrderCondition.EQUALS,
                Ratio.percentage(10));

        test.setActualMeasure(new OnlineMeasure(34));
        test.setExpected(38);

        Map<OrderCondition, ToleranceRequired> map = test.getWhatIfToleranceMap();
        assertEquals(0.12, map.get(OrderCondition.GREATER).getDecimal(), 0);
        assertEquals(0.12, map.get(OrderCondition.EQUALS).getDecimal(), 0);

        StringBuilder buf = new StringBuilder();
        test.appendWhatIfTolerance(buf);
        assertEquals(
                "Would have been:" +
                "----------------" +
                "equals if tolerance >= 12.000 %" +
                "greater if tolerance >= 12.000 %",
            buf.toString().replaceAll(System.lineSeparator(), ""));
    }

    @Test
    public void shouldValidateWhatIfAlgorithmGivinTooHighPercentages() {
        AbstractAssertionErrorImpl test = new AbstractAssertionErrorImpl(
                CName.EMPTY.append("test"),
                OrderCondition.EQUALS,
                Ratio.percentage(10));

        test.setActualMeasure(new OnlineMeasure(34));
        test.setExpected(3);

        Map<OrderCondition, ToleranceRequired> map = test.getWhatIfToleranceMap();
        assertEquals(1034, map.get(OrderCondition.LESS).getPercentage(), 0);
        assertEquals(1034, map.get(OrderCondition.EQUALS).getPercentage(), 0);

        StringBuilder buf = new StringBuilder();
        test.appendWhatIfTolerance(buf);
        assertEquals(
                "Would have been:" +
                "----------------" +
                "equals if tolerance >= 1034.000 %" +
                "less if tolerance >= 1034.000 %",
            buf.toString().replaceAll(System.lineSeparator(), ""));
    }

    @Test
    public void shouldReturnGetTolerance() {
        Ratio tolerance = Ratio.percentage(55);

        AbstractAssertionErrorImpl test = new AbstractAssertionErrorImpl(
                CName.EMPTY.append("test"),
                OrderCondition.EQUALS,
                tolerance);

        assertEquals(tolerance, test.getTolerance());
    }

    @Test
    public void shouldReturnTheTitle() {
        StaticPath title = CName.EMPTY.append("test_12345_xyz");

        AbstractAssertionErrorImpl test = new AbstractAssertionErrorImpl(
                title,
                OrderCondition.EQUALS,
                Ratio.percentage(55));

        assertEquals(title, test.getTitle());
    }

    @Test
    public void shouldReturnCondition() {
        OrderCondition condition = OrderCondition.EQUALS;

        AbstractAssertionErrorImpl test = new AbstractAssertionErrorImpl(
                CName.EMPTY.append("test"), condition,
                Ratio.percentage(55));

        assertEquals(condition, test.getCondition());
    }

}
