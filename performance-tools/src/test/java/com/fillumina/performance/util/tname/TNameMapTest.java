package com.fillumina.performance.util.tname;

import com.fillumina.performance.executor.TN;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMapTest {

    private static class TNamedImpl implements TNamed {
        private final TName name;
        private final int value;

        public TNamedImpl(int value, String... name) {
            this.name = TN.tname(name);
            this.value = value;
        }

        public TNamedImpl(int value, TName name) {
            this.name = name;
            this.value = value;
        }

        @Override
        public TName getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "TNamedImpl{" + "name=" + name + ", value=" + value + '}';
        }
    }

    @Test
    public void shouldGetWithStringPath() {
        TNameMap<TNamedImpl> map = new TNameMap<>();
        map.add(new TNamedImpl(12, "one", "two"));

        assertEquals(12, map.get("one", "two").getValue(), 0);
    }

    @Test
    public void shouldGetWithSingleString() {
        TNameMap<TNamedImpl> map = new TNameMap<>();
        map.add(new TNamedImpl(1, "one"));
        map.add(new TNamedImpl(2, "two"));

        assertEquals(1, map.get("one").getValue(), 0);
        assertEquals(2, map.get("two").getValue(), 0);
    }

    @Test
    public void shouldGetWithSingleStringUnmodifiable() {
        TNameMap<TNamedImpl> map = new TNameMap<>();
        map.add(new TNamedImpl(1, "one"));
        map.add(new TNamedImpl(2, "two"));

        Map<TName,TNamedImpl> unmodifiable = map.unmodifiable();

        assertEquals(1, unmodifiable.get("one").getValue(), 0);
        assertEquals(2, unmodifiable.get("two").getValue(), 0);
    }

    @Test
    public void shouldGetWithTName() {
        TName one = TName.ROOT.append("one");
        TName two = TName.ROOT.append("two");
        TName twelve = TName.ROOT.append("one", "two");

        TNameMap<TNamedImpl> map = new TNameMap<>();
        map.add(new TNamedImpl(1, one));
        map.add(new TNamedImpl(2, two));
        map.add(new TNamedImpl(12, twelve));

        assertEquals(1, map.get(one).getValue(), 0);
        assertEquals(2, map.get(two).getValue(), 0);
        assertEquals(12, map.get(twelve).getValue(), 0);
    }

}
