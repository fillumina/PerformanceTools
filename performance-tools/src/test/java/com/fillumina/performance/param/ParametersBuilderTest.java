package com.fillumina.performance.param;

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
public class ParametersBuilderTest {

    @Test
    public void shouldCreateOneParameter() {

        LinkedTree<String, Object> tree =
                new ParametersBuilder<LinkedTree<String,Object>>()
                        .addParameter("list")
                            .addValue("array", new ArrayList<>())
                            .addValue("linked", new LinkedList<>())
                        .endParameter()
                        .addParameter("size")
                            .addValue("small", 10)
                            .addValue("big", 1000)
                        .endParameter()
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
