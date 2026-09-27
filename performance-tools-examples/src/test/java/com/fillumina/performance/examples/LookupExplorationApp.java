package com.fillumina.performance.examples;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The exploratory mode: a {@code static main} that produces information rather
 * than a verdict.
 * <p>
 * Use this when you do not yet know what to assert. The question is not "did
 * something regress" but "why is this slow", and the useful output is a table
 * of ratios with per-test standard deviation, uncertainty and a significance
 * column, so you can tell a real difference from noise. A stopwatch cannot do
 * this; it gives you one number and no indication of whether the number means
 * anything.
 * <p>
 * This example measures the same lookup two ways, at an easy position and at
 * the worst position. The result is diagnostic rather than merely comparative:
 * the linear scan is 7x slower near the front and 133x slower at the back,
 * while the map stays flat. That identifies the cost as the scan, which is
 * something the absolute number alone would never have told you.
 * <p>
 * The workflow this supports is: explore here, then encode what you learned as an
 * assertion in a test, which is what
 * {@link com.fillumina.performance.examples.template.SearchTypePerformanceTest} does.
 * <p>
 * Note there are deliberately no assertions here. In this mode the framework is
 * an instrument, not a gate.
 */
public class LookupExplorationApp extends PerformanceTemplate {

    private static final String[] NAMES = buildNames();
    private static final Map<String, Integer> INDEX = buildIndex();
    private static final int EASY = 7;
    private static final int HARD = 193;

    private static String[] buildNames() {
        final String[] copy = Arrays.copyOf(Locale.getISOCountries(), 200);
        Arrays.sort(copy);
        return copy;
    }

    private static Map<String, Integer> buildIndex() {
        final Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < NAMES.length; i++) {
            map.put(NAMES[i], i);
        }
        return map;
    }

    private static int linearSearch(final String key) {
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }

    private static int hashLookup(final String key) {
        final Integer value = INDEX.get(key);
        return value == null ? -1 : value;
    }

    public LookupExplorationApp() {
        if (NAMES.length <= HARD) {
            throw new IllegalStateException("need at least " + (HARD + 1) + " names");
        }
    }

    public static void main(final String[] args) {
        new LookupExplorationApp().executeWithFullOutput();
    }

    @Override
    public void config(final MixedConfigurationBuilder<?> config) {
        config.speedConfig()
                .setFixedSamples(33)
                .end();
    }

    @Override
    public void addTests(final TestConfiguration<?> tests) {
        tests.addTest("linear_easy", () -> Sink.drain(linearSearch(NAMES[EASY])));
        tests
                .addTest("hash_easy", () -> Sink.drain(hashLookup(NAMES[EASY])))
                .addTest("linear_hard", () -> Sink.drain(linearSearch(NAMES[HARD])))
                .addTest("hash_hard", () -> Sink.drain(hashLookup(NAMES[HARD])));
    }

    @Override
    public void addAssertions(final MixedAssertionBuilder<?> assertions) {
        // Intentionally empty: this application explores, it does not gate.
    }
}
