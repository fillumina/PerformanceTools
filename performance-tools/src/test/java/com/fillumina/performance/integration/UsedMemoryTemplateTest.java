package com.fillumina.performance.integration;

import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.MemUnit;
import java.util.HashMap;
import java.util.Map;


/**
 * Test the memory used by two structure under 3 different size given
 * as a {@link Sequence}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemoryTemplateTest extends PerformanceTemplate {
    private static final String LINKED_TREE = "LinkdeTree";
    private static final String HASH_MAP = "HashMap";
    private static final String VALUE_1 = "1";
    private static final String VALUE_2 = "5";
    private static final String VALUE_3 = "10";
    private static final String MESSAGE = "hello world";

    public static void main(final String[] args) {
        new UsedMemoryTemplateTest()
                .executeWithFullOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.usedMemConfig().setFixedSamples(1);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest(HASH_MAP, new Runnable() {

                @Sequence
                private Integer times;

                @Override
                public void run() {
                    Map<Integer,Map<Integer,String>> map = new HashMap<>();
                    for (int i=0; i<times; i++) {
                        final HashMap<Integer, String> m = new HashMap<>();
                        m.put(i, MESSAGE);
                        map.put(i, m);
                    }
                }
            })
            .addTest(LINKED_TREE, new Runnable() {

                @Sequence
                private Integer times;

                @Override
                public void run() {
                    LinkedTree<Integer,String> tree = new LinkedTree<>();
                    for (int i=0; i<times; i++) {
                        tree.add(i,null).put(i, MESSAGE);
                    }
                }
            })
            .sequences()
                .name("times")
                    .value(VALUE_1, 1)
                    .value(VALUE_2, 5)
                    .value(VALUE_3, 10);

    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions.usedMemory()
                .forTest(VALUE_1)
                    .order(HASH_MAP).greaterThan(LINKED_TREE)
                .forTest(VALUE_3)
                    .percentage(LINKED_TREE).lessThan(Ratio.percentage(50))
                    .value(HASH_MAP).equalsTo(MemUnit.B.quantity(2_048.0))
                .end();
    }

}
