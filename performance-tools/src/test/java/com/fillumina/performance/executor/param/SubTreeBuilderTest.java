package com.fillumina.performance.executor.param;

import com.fillumina.performance.util.collection.LinkedTree;
import java.util.ArrayList;
import java.util.LinkedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SubTreeBuilderTest {

    @Test
    public void shouldCreateParameters() {

        LinkedTree<String, Object> tree =
                new SubTreeBuilder<LinkedTree<String,Object>>()
                        .name("list")
                            .value("array", new ArrayList<>())
                            .value("linked", new LinkedList<>())
                        .end()
                        .name("size")
                            .value("small", 10)
                            .value("big", 1000)
                        .end()
                        .end();

        assertEquals(2, tree.size(), 0);
        LinkedTree<String,Object> listTree = tree.getTreeAtIndex(0);
        assertEquals("list", listTree.getKey());

        assertEquals("array", listTree.getTree("array").getKey());
        assertTrue(listTree.getTree("array").getValue() instanceof ArrayList);

        assertEquals("linked", listTree.getTree("linked").getKey());
        assertTrue(listTree.getTree("linked").getValue() instanceof LinkedList);


        LinkedTree<String, Object> sizeTree = tree.getTreeAtIndex(1);

        assertEquals("size", sizeTree.getKey());

        assertEquals("small", sizeTree.getTree("small").getKey());
        assertEquals(10, (int)sizeTree.getTree("small").getValue(), 0);

        assertEquals("big", sizeTree.getTree("big").getKey());
        assertEquals(1000, (int)sizeTree.getTree("big").getValue(), 0);
    }
}
