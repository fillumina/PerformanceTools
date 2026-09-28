package com.fillumina.performance.examples;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.stats.Ratio;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * A performance gate over shipped code rather than over synthetic loops.
 * <p>
 * {@link IndexedHashMap} is a class this library actually ships, and it is
 * used on the hot path of every test that declares more than one measurement.
 * {@link java.util.HashMap} is the class that real applications actually use.
 * Comparing the two measures the artefact on a real system, in a real
 * environment, instead of measuring a proxy that resembles the code.
 * <p>
 * The gate is the ordering assertion, not the absolute numbers: absolutes
 * depend on the machine and are not portable, whereas "the shipped map is
 * not faster than the JDK one" holds anywhere and is what a reader can act
 * on. The measured ratio is printed so a human sees the magnitude.
 * <p>
 * On the machine this was written on, the shipped {@code IndexedHashMap} is
 * roughly 29% slower than {@code HashMap} for string-keyed lookup. That is
 * reported, not asserted away.
 */
public class ShippedCodeGateTest extends PerformanceTemplate {

    private static final int KEYS = 200;

    private final String[] keys = new String[KEYS];
    private final Map<String, String> jdkMap = new HashMap<>();
    private final IndexedHashMap<String, String> indexedMap = new IndexedHashMap<>();

    public ShippedCodeGateTest() {
        for (int i = 0; i < KEYS; i++) {
            final String key = "shipped-code-key-" + i;
            keys[i] = key;
            jdkMap.put(key, "value-" + i);
            indexedMap.add(key, "value-" + i);
        }
    }

    public static void main(String[] args) {
        new ShippedCodeGateTest().executeWithFullOutput();
    }

    @Test
    public void shouldNotBeFasterThanTheJdkMap() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedConfig()
                .setFixedSamples(33)
                .end();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("jdkHashMapLookup", () -> {
            for (int i = 0; i < KEYS; i++) {
                Sink.drain(jdkMap.get(keys[i]));
            }
        })
        .addTest("indexedHashMapLookup", () -> {
            for (int i = 0; i < KEYS; i++) {
                Sink.drain(indexedMap.get(keys[i]));
            }
        });
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions
                .tolerance(Ratio.percentage(10))
                .avgTime()
                    .order("jdkHashMapLookup").lessThan("indexedHashMapLookup")
                .end();
    }
}
