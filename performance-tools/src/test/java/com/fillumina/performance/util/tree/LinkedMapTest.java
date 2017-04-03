package com.fillumina.performance.util.tree;

import static com.fillumina.performance.infrastructure.Sink.drain;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.rnd.XorShiftPlusRandom;
import com.fillumina.performance.util.tree.LinkedMap.LEntry;
import com.fillumina.performance.util.tree.LinkedMap.LinkedEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedMapTest extends AbstractMapTest {

    @Override
    protected <K, V> LinkedMap<K, V> createMap() {
        return new LinkedMap<>();
    }

    @Test
    public void shouldAddACustomEntry() {
        class CustomLEntry extends LEntry<Integer,String> {
            public CustomLEntry(Integer key, String value) {
                super(key, value);
            }
        }

        LinkedMap<Integer,String> map = new LinkedMap<>();
        map.put(5, "five");
        map.addEntry(new CustomLEntry(12, "twelve"));
        map.put(7, "seven");

        assertEquals("twelve", map.get(12));
    }

    @Test
    public void shouldGetTheCustomEntryInserted() {
        class CustomLEntry extends LEntry<Integer,String> {
            public CustomLEntry(Integer key, String value) {
                super(key, value);
            }
        }

        LinkedMap<Integer,String> map = new LinkedMap<>();
        map.put(5, "five");
        map.addEntry(new CustomLEntry(12, "twelve"));
        map.put(7, "seven");

        LinkedEntry<Integer,String> entry = map.getEntryWithKey(12);
        assertTrue(entry instanceof CustomLEntry);
    }

    @Test
    public void shouldNotSubstituteAnExistingEntry() {
        class CustomLEntry extends LEntry<Integer,String> {
            public CustomLEntry(Integer key, String value) {
                super(key, value);
            }
        }

        LinkedMap<Integer,String> map = new LinkedMap<>();
        map.put(12, "12");
        map.addEntry(new CustomLEntry(12, "twelve"));

        LinkedEntry<Integer,String> entry = map.getEntryWithKey(12);
        assertFalse(entry instanceof CustomLEntry);
    }

    @Test
    public void shouldGetTheEntryAtTheSpecificIndex() {
        LinkedMap<Integer,String> map = new LinkedMap<>();

        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        assertEquals(1, map.getEntryAtIndex(0).getKey(), 0);
        assertEquals(2, map.getEntryAtIndex(1).getKey(), 0);
        assertEquals(3, map.getEntryAtIndex(2).getKey(), 0);
    }

    public static void main(final String[] args) {
        final Random rnd = new XorShiftPlusRandom();
        final int size = 5;

        class MapTestable extends Testable {
            private Map<Integer,Integer> map;

            public MapTestable(Map<Integer, Integer> map) {
                this.map = map;
            }

            @Override
            public void setUp() {
                for (int i=0; i<size; i++) {
                    map.put(i, i);
                }
            }

            @Override
            public void test() {
                drain(map.get(rnd.nextInt(size)));
            }
        }

        new PerformanceTemplate() {
            @Override
            public void addAssertions(ProgressionAssertion assertions) {
            }

            @Override
            public void config(TestConfiguration config) {
                config.speedTestOnly();
            }

            @Override
            public void addTests(TestContainer<Testable> tests) {
                tests.addTest("LinkedHashMap",
                        new MapTestable(new LinkedHashMap<>()));
                tests.addTest("LinkedMap",
                        new MapTestable(new LinkedMap<>()));
            }
        }.executeWithFullOutput();
    }
}
