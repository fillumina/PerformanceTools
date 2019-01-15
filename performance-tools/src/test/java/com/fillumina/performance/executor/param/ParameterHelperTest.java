package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.tname.TName;
import java.util.Iterator;
import java.util.Map.Entry;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterHelperTest {

    public static class ParameterizedRunnable implements Runnable {
        // to see if it passes through constructor
        private double height;

        // to see if it initializes private inner values
        private char c = 'h';

        @Param
        private String name;

        @Param("size")
        private int intvalue;

        public ParameterizedRunnable() {
            height = 123.45;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return intvalue;
        }

        @Override
        public void run() {
        }
    }

    @Test
    public void shouldCreateNewTestsAndCorrectlyInintializedThem() {
        LinkedTree<String, Object> params = LinkedTree.<String,Object>builder()
                .branch("name")
                    .leaf("Bob", "Bob")
                    .leaf("Tom", "Tom")
                .end()
                .branch("size")
                    .leaf("10", 10)
                    .leaf("100", 100)
                    .leaf("1000", 1_000)
                .getRoot();

        //System.out.println(params.toString());

        IndexedHashMap<TName,RunnableContainer> pmap =
                ParameterHelper.createParameterizedRunnables(
                        Param.class, params, new ParameterizedRunnable());

        //System.out.println(lmap.toString());

        assertEquals(6, pmap.size());

        Iterator<Entry<TName,RunnableContainer>> it = pmap.iterator();
        assertEquals("Bob-10", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Tom-10", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Bob-100", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Tom-100", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Bob-1000", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Tom-1000", it.next().getKey().toStringWithSeparator("-"));
        assertFalse(it.hasNext());

        pmap.forEach((TName tname, RunnableContainer runnable) -> {
            ParameterizedRunnable prunnable =
                    (ParameterizedRunnable) runnable.getRunnable();

            String name = tname.getFirstName();
            assertEquals(name, prunnable.getName());

            int size = Integer.valueOf(tname.getLastName());
            assertEquals(size, prunnable.getSize());
        });
    }

    @Test
    public void shouldCalculateArrayOfListSizes() {
        LinkedTree<String,Object> tree = LinkedTree.<String,Object>builder()
                .branch("0")
                    .leaf("One", 1)
                    .leaf("Two", 2)
                .end()
                .branch("1")
                    .leaf("alpha", 'a')
                    .leaf("beta", 'b')
                    .leaf("gamma", 'g')
                .end()
                .branch("2")
                    .leaf("name", "Tom")
                .getRoot();

        assertArrayEquals(new int[]{2, 3, 1},
                ParameterHelper.calculateBranchesDepth(tree));
    }

    @Test
    public void shouldCreateNewRunnableInstanceWithParametersSet() {
        ParameterizedRunnable runnable = new ParameterizedRunnable();
        RunnableHelper setter =
                new RunnableHelper(runnable, Param.class);

        IndexedHashMap<String,Object> parameters =
                IndexedHashMap.<String,Object>create(
                        "name", "Pippo",
                        "size", 123);

        ParameterizedRunnable result = (ParameterizedRunnable)
                setter.cloneAndSetParameters(parameters);

        assertEquals("Pippo", result.getName());
        assertEquals(123, result.getSize());
        assertEquals(123.45, result.height, 0);
        assertEquals('h', result.c, 0);
    }
}
