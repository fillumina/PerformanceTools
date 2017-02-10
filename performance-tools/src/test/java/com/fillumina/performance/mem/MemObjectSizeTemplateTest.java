package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mem.MemObjectSizeTemplateTest.Creable;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.template.ParameterizedPerformanceTemplate;
import com.fillumina.performance.template.ParameterizedAssertion;
import com.fillumina.performance.template.TestConfiguration;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemObjectSizeTemplateTest extends
        ParameterizedPerformanceTemplate<Creable> {

    private static final String INNER = "inner";
    private static final String STATIC = "static";

    static interface Creable {
        Creable create();
    }

    public static class StaticPerson implements Creable {
        private String name;
        private int age;
        private List<Integer> votes = new ArrayList<>();

        @Override
        public Creable create() {
            return new StaticPerson();
        }
    }

    public class InnerPerson implements Creable {
        private String name;
        private int age;
        private List<Integer> votes = new ArrayList<>();

        @Override
        public Creable create() {
            return new InnerPerson();
        }
    }

    public static void main(final String[] args) {
        new MemObjectSizeTemplateTest().executeWithFullOutput();
    }

    @Override
    public void addParameters(ParameterContainer<Creable> params) {
        params.addParameter(STATIC, new StaticPerson())
                .addParameter(INNER, new InnerPerson());
    }

    @Override
    public void addAssertions(ParameterizedAssertion assertion) {
        assertion
            .usedMem()
                .forTest("test")
                    .withTolerance(5)
                        .assertOrder(STATIC).lessThan(INNER)
                    .end()
                .endTests()
            .allocatedMem()
                .forTest("test")
                    .withTolerance(5)
                        .assertOrder(STATIC).lessThan(INNER)
                    .end()
                .endTests();
    }

    @Override
    public void config(TestConfiguration config) {
        config.usedMemTestOnly()
                .allocatedMemTest();
    }

    @Override
    public void addTests(TestContainer<ParameterizedTestable<Creable>> tests) {
        // it works for both used and allocated memory
        tests.addTest("test", new ParameterizedTestable<Creable>() {
            private Object[] array = new Object[1_000];
            private int index;

            @Override
            public Object test(Creable param) {
                Object obj = param.create();
                array[index] = obj;
                index++;
                return obj;
            }
        });
    }

}
