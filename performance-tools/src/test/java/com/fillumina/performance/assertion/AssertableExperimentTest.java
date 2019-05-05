package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.SingleMeasure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.ImmutableDimensionalMeasure;
import com.fillumina.performance.util.unit.Magnitude;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableExperimentTest {

    public static class AssertableExperimentImpl implements AssertableExperiment {

        private final Map<String, DimensionalMeasure> map;

        public AssertableExperimentImpl(Object ... args) {
            this.map = new HashMap<>();
            for (int i=0; i<args.length; i+=2) {
                final double value = (double) args[i+1];
                map.put((String)args[i], dm(value));
            }
        }

        public AssertableExperimentImpl(Map<String, DimensionalMeasure> map) {
            this.map = map;
        }

        @Override
        public Collection<? extends CharSequence> getNames() {
            return map.keySet();
        }

        @Override
        public DimensionalMeasure getMeasure(CharSequence name) {
            final DimensionalMeasure dm = map.get(name.toString());
            if (dm == null) {
                throw new NoSuchElementException(name.toString());
            }
            return dm;
        }
    }

    @Test
    public void shouldGetNames() {
        AssertableExperimentImpl ae =
                new AssertableExperimentImpl("a", 1.0, "b", 2.0);

        final Collection<? extends CharSequence> names = ae.getNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("a"));
        assertTrue(names.contains("b"));
    }

    @Test
    public void shouldGetEmptyNames() {
        AssertableExperimentImpl ae = new AssertableExperimentImpl();

        final Collection<? extends CharSequence> names = ae.getNames();
        assertTrue(names.isEmpty());
    }

    @Test(expected=NoSuchElementException.class)
    public void shouldReturnNullMeasureIfEmptyNames() {
        AssertableExperimentImpl ae = new AssertableExperimentImpl();

        ae.getMeasure("not_existent");
    }

    @Test(expected=NoSuchElementException.class)
    public void shouldThrowNSEEIfMeasureIfNotPresent() {
        AssertableExperimentImpl ae =
                new AssertableExperimentImpl("a", 1.0, "b", 2.0);

        assertNull(ae.getMeasure("not_existent"));
    }

    @Test
    public void shouldGetMeasure() {
        AssertableExperimentImpl ae =
                new AssertableExperimentImpl("a", 1.0, "b", 2.0);

        assertEquals(dm(1.0), ae.getMeasure("a"));
        assertEquals(dm(2.0), ae.getMeasure("b"));
    }

    @Test
    public void shouldBeEmpty() {
        AssertableExperimentImpl ae = new AssertableExperimentImpl();

        assertTrue(ae.isEmpty());
    }

    @Test
    public void shouldGetFirstMeasure() {
        AssertableExperimentImpl ae =
                new AssertableExperimentImpl("a", 1.0, "b", 2.0);

        assertEquals(dm(1.0), ae.getFirstMeasure());
    }

    @Test(expected=NoSuchElementException.class)
    public void shouldReturnNullFirstMeasureIfEmpty() {
        AssertableExperimentImpl ae = new AssertableExperimentImpl();

        ae.getFirstMeasure();
    }

    private static DimensionalMeasure dm(double value) {
        return new ImmutableDimensionalMeasure(new SingleMeasure(value),
                Magnitude.UNIT);
    }
}
