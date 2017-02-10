package com.fillumina.performance.template.speed;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.template.ParameterizedPerformanceTemplate;
import com.fillumina.performance.template.ParameterizedAssertion;
import com.fillumina.performance.template.TestConfiguration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiThreadedProgressionTemplateTest
        extends ParameterizedPerformanceTemplate<Map<Integer,Integer>> {
    private final int SIZE = 1_000;

    public static void main(final String[] args) {
        new MultiThreadedProgressionTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration config) {
        config.speedTestOnly()
                .setTimeoutSeconds(3600)
                .setMaxPercentageMargin(10)
                .setDefaultMultiThreadedMode();
    }

    @Override
    public void addParameters(
            ParameterContainer<Map<Integer, Integer>> params) {
        params.addParameter("ConcurrentHashMap",
                new ConcurrentHashMap<Integer, Integer>());
//        params.addParameter("SynchronizedHashMap",
//                Collections.synchronizedMap(new HashMap<Integer,Integer>()));
    }

    @Override
    public void addAssertions(ParameterizedAssertion assertion) {
    }

    @Override
    public void addTests(
            TestContainer<ParameterizedTestable<Map<Integer, Integer>>> tests) {
        tests.addTest("test", new ParameterizedTestable<Map<Integer, Integer>>() {

            @Override
            public void onBeforeSample(Map<Integer, Integer> param,
                    int iterations) {
                for (int i=0; i<SIZE; i++) {
                    param.put(i, i);
                }
            }

            @Override
            public Object test(Map<Integer, Integer> param) {
                int index = ThreadLocalRandom.current().nextInt(SIZE);
                return param.get(index);
            }
        });
    }
}
