/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fillumina.performance.util;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TreeHolderTest {

    @Test
    public void shouldBeEmptyIfEmptyMapIsInserted() {
        Map<ComposedName,String> map = new HashMap<>();

        TreeHolder<String,Map<ComposedName,String>> holder = new TreeHolder<>(map);

        assertTrue(holder.isEmpty());
    }

    @Test
    public void shouldNotBeEmptyIfMapIsInserted() {
        Map<ComposedName,String> map = new HashMap<>();
        insert(map, "1");
        insert(map, "2");

        TreeHolder<String,Map<ComposedName,String>> holder = new TreeHolder<>(map);

        assertFalse(holder.isEmpty());
    }

    @Test
    public void shouldReturnTheName() {
        Map<ComposedName,String> map = new HashMap<>();
        insert(map, "1");
        insert(map, "2");

        final ComposedName name = ComposedName.create("name");

        TreeHolder<String,Map<ComposedName,String>> holder = new TreeHolder<>(name, map);

        assertEquals(name, holder.getName());
    }

    @Test
    public void shouldReturnTheNameAndTheTree() {
        Map<ComposedName,String> map = new HashMap<>();
        insert(map, "1");
        insert(map, "2");

        final ComposedName name = ComposedName.create("name");

        TreeHolder<String,Map<ComposedName,String>> holder = new TreeHolder<>(name, map);

        assertEquals(name, holder.getName());
        assertEquals(map, holder.getTree());
    }

    @Test
    public void shouldReturnTheTree() {
        Map<ComposedName,String> map = new HashMap<>();
        insert(map, "1");
        insert(map, "2");

        TreeHolder<String,Map<ComposedName,String>> holder = new TreeHolder<>(map);

        assertEquals(map, holder.getTree());
    }

    @Test
    public void shouldGetTheElement() {
        Map<ComposedName,String> map = new HashMap<>();
        ComposedName one = ComposedName.create("one");
        String value = "1";
        map.put(one, value);

        TreeHolder<String,Map<ComposedName,String>> holder = new TreeHolder<>(map);

        String result = (String) holder.get(one);
        assertEquals(value, result);
    }

    @Test
    public void shouldGetTheElementInATwoLevelTree() {
        Map<ComposedName,Map<ComposedName,String>> map = new HashMap<>();
        ComposedName one = ComposedName.create("one");
        Map<ComposedName,String> mapOne = new HashMap<>();
        map.put(one, mapOne);

        ComposedName oneOne = one.append("one");
        String oneOneValue = "11";
        mapOne.put(oneOne, oneOneValue);

        ComposedName oneTwo = one.append("two");
        String oneTwoValue = "12";
        mapOne.put(oneTwo, oneTwoValue);

        TreeHolder<String,Map<ComposedName,Map<ComposedName,String>>> holder = new TreeHolder<>(map);

        @SuppressWarnings("unchecked")
        Map<ComposedName,String> resultMap = (Map<ComposedName,String>)
                holder.get(one);

        assertEquals(2, resultMap.size());

        assertEquals(oneOneValue, holder.get(oneOne));
        assertEquals(oneTwoValue, holder.get(oneTwo));
    }

    private void insert(Map<ComposedName, String> map, final String value) {
        map.put(ComposedName.create(value), value);
    }
}
