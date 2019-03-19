package com.fillumina.performance.util;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SelfTest {

    public static class Parent<I extends Parent<?>> {

        @SuppressWarnings("unchecked")
        public I setName(String name) {
            return (I) this;
        }
    }

    public static class Child extends Parent<Child> {

        public Child setColor(String color) {
            return this;
        }
    }

    @Test
    public void shouldTheParentFluentInterfaceBeUsableFromChildren() {
        Child c = new Child().setName("Pippo").setColor("blue");

        assertNotNull(c);
    }

}
