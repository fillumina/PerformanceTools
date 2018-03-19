package com.fillumina.performance.util.collection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnmodifiableListTest {

    @Test
    public void shouldArrayAsBeWritable() {
        List<String> list = Arrays.asList("one", "two", "three");
        list.set(1, "modified");

        assertEquals("modified", list.get(1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotUnmodifiableListBeWritable() {
        List<String> list = new UnmodifiableList<>("one", "two", "three");
        list.set(1, "modified");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotUnmodifiableListBeRemovable() {
        List<String> list = new UnmodifiableList<>("one", "two", "three");
        list.remove(2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotUnmodifiableListBeRemovableByIterator() {
        List<String> list = new UnmodifiableList<>("one", "two", "three");
        Iterator<String> it = list.iterator();
        it.next();
        it.remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotUnmodifiableListBeAddable() {
        List<String> list = new UnmodifiableList<>("one", "two", "three");
        list.add("four");
    }

}
