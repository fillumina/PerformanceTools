package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.tname.TName;
import java.util.Iterator;
import java.util.Map;
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

        @Param
        private String name;

        @Param("size")
        private int intvalue;

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

        LinkedMap<TName,Runnable> lmap =
                ParameterHelper.createParameterizedRunnable(
                        new ParameterizedRunnable(),
                        params, Param.class);

        //System.out.println(lmap.toString());

        assertEquals(6, lmap.size());

        Iterator<Entry<TName,Runnable>> it = lmap.iterator();
        assertEquals("Bob-10", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Tom-10", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Bob-100", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Tom-100", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Bob-1000", it.next().getKey().toStringWithSeparator("-"));
        assertEquals("Tom-1000", it.next().getKey().toStringWithSeparator("-"));
        assertFalse(it.hasNext());

        for (Map.Entry<TName, Runnable> entry: lmap) {
            TName sp = entry.getKey();
            ParameterizedRunnable runnable =
                    (ParameterizedRunnable) entry.getValue();

            String name = sp.getFirstName();
            assertEquals(name, runnable.getName());

            int size = Integer.valueOf(sp.getLastName());
            assertEquals(size, runnable.getSize());
        }
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

        LinkedMap<String,Object> parameters = LinkedMap.<String,Object>create(
                        "name", "Pippo",
                        "size", 123);

        ParameterizedRunnable result = (ParameterizedRunnable)
                ParameterHelper
                        .setParameters(setter.doClone(), parameters);

        assertEquals("Pippo", result.getName());
        assertEquals(123, result.getSize());
    }
}
