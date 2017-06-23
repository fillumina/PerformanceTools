package com.fillumina.performance.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReflectionHelper {

    /** @return all fields including inherited and private. */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        Class<?> c = clazz;
        do {
            for (Field f : c.getDeclaredFields()) {
                list.add(f);
            }
            c = c.getSuperclass();
        } while (c != null && c != Runnable.class);
        return list;
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        for (Field f : getAllFields(obj.getClass())) {
            if (fieldName.equals(f.getName())) {
                try {
                    f.setAccessible(true);
                    return f.get(obj);
                } catch (IllegalArgumentException |
                        IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        throw new IllegalArgumentException("field '" + fieldName +
            "' not present in class " + obj.getClass().getCanonicalName());
    }

    public static Object newInstance(Object target) {
        final Class<?> clazz = target.getClass();
        Object obj = createWithDefaultConstructor(clazz);
        if (obj != null) {
            return obj;
        }
        obj = createFromAnonymousClass(target);
        if (obj != null) {
            return obj;
        }
        throw new RuntimeException("cannot create new class");
    }

    public static Runnable createWithDefaultConstructor(Class<?> clazz) {
        for (Constructor<?> c : clazz.getConstructors()) {
            if (c.getParameterCount() == 0) {
                try {
                    return (Runnable) c.newInstance();
                } catch (InstantiationException |
                        IllegalAccessException |
                        IllegalArgumentException |
                        InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return null;
    }

    public static Object createFromAnonymousClass(Object target) {
        Class<?> clazz = target.getClass();
        Class<?> enclosingClass = clazz.getEnclosingClass();
        if (enclosingClass == null) {
            return null;
        }
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            Parameter[] parameters = c.getParameters();
            if (parameters.length != 0 &&
                    enclosingClass == parameters[0].getType()) {
                Field f = getFieldValueWithType(clazz, enclosingClass);
                try {
                    f.setAccessible(true);
                    Object enclosing = f.get(target);
                    c.setAccessible(true);
                    return c.newInstance(enclosing);
                } catch (IllegalArgumentException |
                        IllegalAccessException | InstantiationException |
                        InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                try {
                    c.setAccessible(true);
                    return c.newInstance();
                } catch (IllegalArgumentException |
                        IllegalAccessException | InstantiationException |
                        InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return null;
    }

    private static Field getFieldValueWithType(Class<?> clazz, Class<?> type) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getType() == type) {
                return f;
            }
        }
        return null;
    }
}
