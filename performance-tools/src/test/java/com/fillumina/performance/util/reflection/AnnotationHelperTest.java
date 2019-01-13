package com.fillumina.performance.util.reflection;

import com.fillumina.performance.util.FieldAnnotation;
import com.fillumina.performance.util.MethodAnnotation;
import java.lang.reflect.Field;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnnotationHelperTest {

    private boolean called = false;
    private String param;

    @FieldAnnotation
    private String field1;

    @FieldAnnotation
    private String field2;

    @MethodAnnotation
    public void callable() {
        called = true;
    }

    @MethodAnnotation
    public void callableWithParam(String param) {
        this.param = param;
    }

    @Test
    public void shouldSetField() {
        assertNull(field1);
        List<Field> fields =
                AnnotationHelper.getFields(this, FieldAnnotation.class);
        Field f = fields.get(0);
        AnnotationHelper.setField(this, f, "here!");
        assertEquals("here!", field1);
    }

    @Test
    public void shouldGetFields() {
        List<Field> fields =
                AnnotationHelper.getFields(this, FieldAnnotation.class);
        assertEquals(2, fields.size());
    }

    @Test
    public void shouldCallMethods() {
        assertNull(param);
        AnnotationHelper.callMethods(this, MethodAnnotation.class, "hello");
        assertEquals("hello", param);
    }

}
