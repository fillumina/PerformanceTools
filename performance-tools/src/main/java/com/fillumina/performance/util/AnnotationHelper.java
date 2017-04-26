package com.fillumina.performance.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnnotationHelper {

    public static void setField(Object target,
            Field field,
            Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalArgumentException |
                IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static List<Field> getFields(Object target,
            Class<? extends Annotation> annotation) {
        List<Field> list = new ArrayList<>();
        for (Field f : ReflectionHelper.getAllFields(target.getClass()) ) {
            if (f.isAnnotationPresent(annotation)) {
                list.add(f);
            }
        }
        return list;
    }

    public static Object callMethods(Object target,
            Class<? extends Annotation> annotation,
            Object... params) {
        boolean called = false;
        for (Method method :
                findMethodsAnnotatedWith(target.getClass(), annotation)) {
            if (method != null) {
                try {
                    return method.invoke(target, params);
                } catch (IllegalAccessException | IllegalArgumentException |
                        InvocationTargetException ex) {
                    // do nothing
                }
            }
        }
        return null;
    }

    private static List<Method> findMethodsAnnotatedWith(Class<?> target,
            Class<? extends Annotation> annotation) {
        List<Method> list = new ArrayList<>();
        for (Method m : target.getMethods()) {
            if (m.isAnnotationPresent(annotation)) {
                list.add(m);
            }
        }
        return list;
    }
}
