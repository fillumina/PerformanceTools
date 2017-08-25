package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureTimesValue;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated // TODO REMOVE just an experiment erroneously committed
public class MeasureCalculator {

    private Deque<StackItem<?>> stack = new ArrayDeque<>();

    private interface StackItem<T> {
        void exec(Deque<StackItem<?>> stack);
        T get();
    }

    private static abstract class Value<T> implements StackItem<T> {
        @Override
        public void exec(Deque<StackItem<?>> stack) {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static abstract class Operation implements StackItem<Void> {
        @Override
        public Void get() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class ValueOp extends Value<Double> {
        private final double value;

        public ValueOp(double value) {
            this.value = value;
        }

        @Override
        public Double get() {
            return value;
        }
    }

    private static class TestMeasureOp extends Value<Measure> {
        private final TName test;

        public TestMeasureOp(TName test) {
            this.test = test;
        }

        @Override
        public Measure get() {
            return null;
        }
    }

    private static class MeasureOp extends Value<Measure> {
        private final Measure measure;

        public MeasureOp(Measure measure) {
            this.measure = measure;
        }

        @Override
        public Measure get() {
            return measure;
        }
    }

    private static class MeasureTimesValueOp extends Operation {

        @Override
        public void exec(Deque<StackItem<?>> stack) {
            StackItem<?> op1 = stack.pop();
            StackItem<?> op2 = stack.pop();
            TestMeasureOp measure;
            ValueOp value;
            if (op1 instanceof TestMeasureOp) {
                value = (ValueOp) op2;
                measure = (TestMeasureOp) op1;
            } else {
                value = (ValueOp) op1;
                measure = (TestMeasureOp) op2;
            }
            Measure result = new MeasureTimesValue(measure.get(), value.get());
            stack.push(new MeasureOp(result));
        }
    }

    private static class MeasureDividedByValueOp extends Operation {
        @Override
        public void exec(Deque<StackItem<?>> stack) {
            StackItem<?> op1 = stack.pop();
            StackItem<?> op2 = stack.pop();
            TestMeasureOp measure;
            ValueOp value;
            if (op1 instanceof TestMeasureOp) {
                value = (ValueOp) op2;
                measure = (TestMeasureOp) op1;
            } else {
                value = (ValueOp) op1;
                measure = (TestMeasureOp) op2;
            }
            Measure result = new MeasureTimesValue(measure.get(), 1/value.get());
            stack.push(new MeasureOp(result));
        }
    }
}
