package com.fillumina.performance.util.reflection;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReflectionHelperTest {

    public static class A {
        private int privateVar;
        public double publicVar;
        protected String protectedVar;

        public static short publicStaticVar;
        private static char privateStaticVar;
        protected static long protectedStaticVar;
    }

    @Test
    public void shouldGetAllFields() {
        List<Field> fields = ReflectionHelper.getAllFields(A.class);

        assertEquals(6, fields.size(), 0);

        List<String> names = fields.stream()
                .map( f -> f.getName() )
                .collect( Collectors.toList() );

        assertTrue(names.contains("privateVar"));
        assertTrue(names.contains("publicVar"));
        assertTrue(names.contains("protectedVar"));
        assertTrue(names.contains("privateStaticVar"));
        assertTrue(names.contains("publicStaticVar"));
        assertTrue(names.contains("protectedStaticVar"));
    }

    @Test
    public void shouldGetFieldValue() {
        A a = new A();
        init(a);

        assertEquals("public var not read",
                1.234, ReflectionHelper.getFieldValue(a, "publicVar"));

        assertEquals("private var not read",
                56, ReflectionHelper.getFieldValue(a, "privateVar"));

        assertEquals("protected var not read",
                "hello", ReflectionHelper.getFieldValue(a, "protectedVar"));

        assertEquals("public static var not read",
                (short)12, ReflectionHelper.getFieldValue(a, "publicStaticVar"));

        assertEquals("private static var not read",
                'c', ReflectionHelper.getFieldValue(a, "privateStaticVar"));

        assertEquals("protected static var not read",
                987L, ReflectionHelper.getFieldValue(a, "protectedStaticVar"));
    }

    private void init(A a) {
        a.publicVar = 1.234;
        a.privateVar = 56;
        a.protectedVar = "hello";
        A.publicStaticVar = 12;
        A.protectedStaticVar = 987L;
        A.privateStaticVar = 'c';
    }

    @Test
    public void shouldCreateWithDefaultConstructor() {
        A a = new A();
        init(a);
        A obj = ReflectionHelper.newInstance(a);

        assertEquals(a.getClass(), obj.getClass());
        assertFalse(a == obj);
    }

    interface B {
        public int getSome();
    }

    @Test
    public void shouldCreateFromAnonymousClassWithNoReferences() {
        B b = new B() {
            @Override public int getSome() { return 77; }
        };

        assertEquals(77, b.getSome(), 0);

        B obj = ReflectionHelper.newInstance(b);

        assertEquals(obj.getClass(), b.getClass());
        assertEquals(77, obj.getSome(), 0);
    }

    private int[] classField = new int[88];

    @Test
    public void shouldCreateFromAnonymousClassWithReferenceToClassField() {
        B b = new B() {
            @Override public int getSome() { return classField.length; }
        };

        assertEquals(88, b.getSome(), 0);

        B obj = ReflectionHelper.newInstance(b);

        assertEquals(obj.getClass(), b.getClass());
        assertEquals(88, obj.getSome(), 0);
    }

    @Test
    public void shouldFillValueOfAnonymousClassWithReferenceToLocalVar() {
        int cLocalVar[] = new int[3];
        int bLocalVar[] = new int[7];
        int aLocalVar[] = new int[17];

        B b = new B() {
            @Override public int getSome() {
                return aLocalVar.length * 5 +
                        bLocalVar.length * 13 +
                        cLocalVar.length * 19;
            }
        };

        assertEquals(233, b.getSome(), 0);

        B obj = ReflectionHelper.newInstance(b);

        assertEquals(obj.getClass(), b.getClass());
        // makes it sure that each variable gets its proper value
        assertEquals(233, obj.getSome(), 0);
    }

}
