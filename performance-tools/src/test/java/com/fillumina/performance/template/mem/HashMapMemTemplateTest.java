package com.fillumina.performance.template.mem;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mem.MemParameterizedTestable;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.template.ParameterizedMixedAssertion;
import com.fillumina.performance.template.ParameterizedPerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.collection.LinkedTree;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HashMapMemTemplateTest
        extends ParameterizedPerformanceTemplate<Integer> {

    public static void main(final String[] args) {
        new HashMapMemTemplateTest()
                .executeWithFullOutput();
    }

    @Override
    public void config(TestConfiguration config) {
        config.allocatedMemTestOnly()
                .setSamples(5);
    }

    @Override
    public void addParameters(ParameterContainer<Integer> params) {
        params.addParameter("0", 0)
                .addParameter("1", 1)
                .addParameter("5", 5)
                .addParameter("10", 10);
    }

    @Override
    public void addAssertions(ParameterizedMixedAssertion assertion) {
    }

    @Override
    public void addTests(TestContainer<ParameterizedTestable<Integer>> tests) {
        tests.addTest("HashMap", new MemParameterizedTestable<Integer>() {
            @Override
            public Object memTest(Integer param) {
                Map<Integer,Map<Integer,String>> map = new HashMap<>();
                for (int i=0; i<param; i++) {
                    final HashMap<Integer, String> m = new HashMap<>();
                    m.put(i, "hello world");
                    map.put(i, m);
                }
                return map;
            }
        });
        tests.addTest("LinkedTree", new MemParameterizedTestable<Integer>() {
            @Override
            public Object memTest(Integer param) {
                LinkedTree<Integer,String> tree = new LinkedTree<>();
                for (int i=0; i<param; i++) {
                    tree.createChild(i,null).put(i, "hello world");
                }
                return tree;
            }
        });
    }

}
