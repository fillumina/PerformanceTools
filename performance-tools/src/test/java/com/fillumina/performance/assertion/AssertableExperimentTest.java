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

    /**
     * Proving that {@link AssertableExperiment } can effectively be
     * implemented by a standard {@link Map<CharSequence,DimensionalMeasure>}.
     */
    public static class AssertableExperimentAsMap
            extends HashMap<String, DimensionalMeasure>
            implements AssertableExperiment {

        public AssertableExperimentAsMap(Object ... args) {
            for (int i=0; i<args.length; i+=2) {
                final double value = (double) args[i+1];
                put((String)args[i], dm(value));
            }
        }

        public AssertableExperimentAsMap(Map<String, DimensionalMeasure> map) {
            super(map);
        }

        @Override
        public Collection<? extends CharSequence> getNames() {
            return keySet();
        }

        @Override
        public DimensionalMeasure getMeasure(CharSequence name) {
            final DimensionalMeasure dm = get(name.toString());
            if (dm == null) {
                throw new NoSuchElementException(name.toString());
            }
            return dm;
        }
    }

    @Test
    public void shouldGetNames() {
        AssertableExperimentAsMap ae =
                new AssertableExperimentAsMap("a", 1.0, "b", 2.0);

        final Collection<? extends CharSequence> names = ae.getNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("a"));
        assertTrue(names.contains("b"));
    }

    @Test
    public void shouldGetEmptyNames() {
        AssertableExperimentAsMap ae = new AssertableExperimentAsMap();

        final Collection<? extends CharSequence> names = ae.getNames();
        assertTrue(names.isEmpty());
    }

    @Test(expected=NoSuchElementException.class)
    public void shouldReturnNullMeasureIfEmptyNames() {
        AssertableExperimentAsMap ae = new AssertableExperimentAsMap();

        ae.getMeasure("not_existent");
    }

    @Test(expected=NoSuchElementException.class)
    public void shouldThrowNSEEIfMeasureIfNotPresent() {
        AssertableExperimentAsMap ae =
                new AssertableExperimentAsMap("a", 1.0, "b", 2.0);

        assertNull(ae.getMeasure("not_existent"));
    }

    @Test
    public void shouldGetMeasure() {
        AssertableExperimentAsMap ae =
                new AssertableExperimentAsMap("a", 1.0, "b", 2.0);

        assertEquals(dm(1.0), ae.getMeasure("a"));
        assertEquals(dm(2.0), ae.getMeasure("b"));
    }

    @Test
    public void shouldBeEmpty() {
        AssertableExperimentAsMap ae = new AssertableExperimentAsMap();

        assertTrue(ae.isEmpty());
    }

    @Test
    public void shouldGetFirstMeasure() {
        AssertableExperimentAsMap ae =
                new AssertableExperimentAsMap("a", 1.0, "b", 2.0);

        assertEquals(dm(1.0), ae.getFirstMeasure());
    }

    @Test(expected=NoSuchElementException.class)
    public void shouldReturnNullFirstMeasureIfEmpty() {
        AssertableExperimentAsMap ae = new AssertableExperimentAsMap();

        ae.getFirstMeasure();
    }

    private static DimensionalMeasure dm(double value) {
        return new ImmutableDimensionalMeasure(new SingleMeasure(value),
                Magnitude.UNIT);
    }
}
