package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.template.SpeedMemPerametrizedSequenceTemplateTest.Creator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedMemPerametrizedSequenceTemplateTest
        extends AutoParametrizedSequencePerformanceTemplate
                    <List<Object>, Creator>{
    private static final int SIZE = 1000;

    interface Creator {
        Object create();
    }

    public static void main(final String[] args) {
        new SpeedMemPerametrizedSequenceTemplateTest().executeWithFullOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration
                .performSpeedTest()
                    .setMaxPercentageMargin(15)
                .endSpeedConfig()
                .performAllocatedMemTest()
                .endMemConfig()
                .performUsedMemTest();
    }

    @Override
    public void addParameters(ParameterContainer<List<Object>> parameters) {
        parameters
                .addParameter("LinkedList", new LinkedList<>())
                .addParameter("ArrayList", new ArrayList<>());
    }

    @Override
    public void addSequence(SequenceContainer<Creator> sequence) {
        sequence.setSequenceItem("integer", new Creator() {
                        @Override
                        public Object create() {
                            // autoboxed into integer
                            return 1234;
                        }
                    })
                .setSequenceItem("string", new Creator() {
                        @Override
                        public Object create() {
                            // it's created so it's not interned
                            return new String("1234");
                        }
                    });
    }

    @Override
    public void addAssertions(ParametrizedSequenceAssertion assertion) {
    }

    @Override
    public void addTests(TestContainer<ParametrizedSequenceTestable
                                <List<Object>, Creator>> tests) {
        tests.addTest("test", new ParametrizedSequenceTestable
                <List<Object>, Creator>() {

            @Override
            public void beforeTest(List<Object> list, Creator creator,
                    int iterations) {
                list.clear();
            }

            @Override
            public Object test(List<Object> map, Creator creator) {
                return map.add(creator.create());
            }
        });
    }
}
