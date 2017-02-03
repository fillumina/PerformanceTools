package com.fillumina.performance.template.speed;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.template.AutoParameterizedSequencePerformanceTemplate;
import com.fillumina.performance.template.ParameterizedSequenceAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.template.speed.SpeedMemPerameterizedSequenceTemplateTest.Creator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedMemPerameterizedSequenceTemplateTest
        extends AutoParameterizedSequencePerformanceTemplate
                    <List<Object>, Creator>{

    public interface Creator {
        Object create();
    }

    public static void main(final String[] args) {
        new SpeedMemPerameterizedSequenceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration
                .speedTestOnly()
                    .setMaxPercentageMargin(15);
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
    public void addAssertions(ParameterizedSequenceAssertion assertion) {
    }

    @Override
    public void addTests(TestContainer<ParameterizedSequenceTestable
                                <List<Object>, Creator>> tests) {
        tests.addTest("test", new ParameterizedSequenceTestable
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
