package com.fillumina.performance.util.pathname;

import com.fillumina.performance.executor.PN;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PathNamedMapTest {

    private static class TNamedImpl implements PathNamed {
        private final PathName name;
        private final int value;

        public TNamedImpl(int value, String... name) {
            this.name = PN.pname(name);
            this.value = value;
        }

        public TNamedImpl(int value, PathName name) {
            this.name = name;
            this.value = value;
        }

        @Override
        public PathName getPathName() {
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
    public void shouldGetWithSingleString() {
        PathNamedMap<TNamedImpl> map = new PathNamedMap<>();
        map.add(new TNamedImpl(1, "one"));
        map.add(new TNamedImpl(2, "two"));

        assertEquals(1, map.get("one").getValue(), 0);
        assertEquals(2, map.get("two").getValue(), 0);
    }

    @Test
    public void shouldGetWithSingleStringUnmodifiable() {
        PathNamedMap<TNamedImpl> map = new PathNamedMap<>();
        map.add(new TNamedImpl(1, "one"));
        map.add(new TNamedImpl(2, "two"));

        Map<PathName,TNamedImpl> unmodifiable = map.unmodifiableView();

        assertEquals(1, unmodifiable.get("one").getValue(), 0);
        assertEquals(2, unmodifiable.get("two").getValue(), 0);
    }

    @Test
    public void shouldGetWithTName() {
        PathName one = PathName.ROOT.append("one");
        PathName two = PathName.ROOT.append("two");
        PathName twelve = PathName.ROOT.append("one", "two");

        PathNamedMap<TNamedImpl> map = new PathNamedMap<>();
        map.add(new TNamedImpl(1, one));
        map.add(new TNamedImpl(2, two));
        map.add(new TNamedImpl(12, twelve));

        assertEquals(1, map.get(one).getValue(), 0);
        assertEquals(2, map.get(two).getValue(), 0);
        // that's its power: it can match whatever CharSequence!
        assertEquals(2, map.get("two").getValue(), 0);

        assertEquals(12, map.get(twelve).getValue(), 0);
        assertEquals(12, map.get("one : two").getValue(), 0);
    }

}
