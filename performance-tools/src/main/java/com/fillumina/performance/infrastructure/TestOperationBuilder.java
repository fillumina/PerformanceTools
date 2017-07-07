package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.CallBackBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestOperationBuilder<C>
        extends CallBackBuilder<C, List<TestOperation>> {

    private final List<TestOperation> list = new ArrayList<>();

    public TestOperationBuilder() {
    }

    public TestOperationBuilder(C caller) {
        super(caller);
    }

    public TestOperationBuilder(Setter<C, List<TestOperation>> setter) {
        super(setter);
    }

    public OperationBuilder test(String a) {
        return new OperationBuilder(a);
    }

    public class OperationBuilder {
        private final String a;

        OperationBuilder(String a) {
            this.a = a;
        }

        public SecondName add() {
            return new SecondName(a, TestOperation.Operation.ADD);
        }

        public SecondName subtract() {
            return new SecondName(a, TestOperation.Operation.SUBTRACT);
        }
    }

    public class SecondName {
        private final String a;
        private final TestOperation.Operation op;

        public SecondName(String a, TestOperation.Operation op) {
            this.a = a;
            this.op = op;
        }

        public TestOperationBuilder<C> test(String b) {
            TestOperation testOperation = new TestOperation(a, b, op);
            list.add(testOperation);
            return TestOperationBuilder.this;
        }
    }

    @Override
    public List<TestOperation> build() {
        return list;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (TestOperation to : list) {
            if (buf.length() != 0) {
                buf.append(", ");
            }
            buf.append(to);
        }
        return buf.toString();
    }
}
