package com.fillumina.performance.util.reflection;

import com.fillumina.performance.util.collection.IndexedHashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static IndexedHashMap<String,Field> getAllFieldsByName(
            Class<?> clazz) {
        IndexedHashMap<String,Field> map = new IndexedHashMap<>();
        getAllFields(clazz).forEach( f -> map.put(f.getName(), f));
        return map;
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
            "' not present in class " + obj.getClass().getName() +
                "\nFIELDS: " + ReflectionHelper
                        .getAllFields(obj.getClass())
                        .stream()
                        .map(f -> f.getName())
                        .collect(Collectors.toList())
                        .toString());
    }

    /** Clones the given object. */
    public static <T> T clone(T object) {
        T clone = newInstance(object);
        for (Field f : ReflectionHelper.getAllFields(object.getClass())) {
            f.setAccessible(true);
            try {
                Object value = f.get(object);
                f.set(clone, value);
            } catch (IllegalArgumentException |
                    IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return clone;
    }

    /** Creates a new instance of the given object. */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(T origin) {
        final Class<?> clazz = origin.getClass();
        Object obj = createWithDefaultConstructor(clazz);
        if (obj != null) {
            return (T) obj;
        }
        obj = createFromAnonymousClass(origin);
        if (obj != null) {
            return (T) obj;
        }
        throw new RuntimeException("cannot create new class");
    }

    @SuppressWarnings("unchecked")
    public static <T> T createWithDefaultConstructor(Class<T> clazz) {
        for (Constructor<?> c : clazz.getConstructors()) {
            if (c.getParameterCount() == 0) {
                try {
                    return (T) c.newInstance();
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

    /**
     * Creates a new instance of an anonymous class.
     */
    @SuppressWarnings("unchecked")
    public static <T> T createFromAnonymousClass(T origin) {
        Class<?> clazz = origin.getClass();
        Class<?> enclosingClass = clazz.getEnclosingClass();
        if (enclosingClass == null) {
            return null;
        }
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            Parameter[] parameters = c.getParameters();
            if (parameters.length == 0) {
                return newInstance(c);
            }
            Object[] vars = getParametersFromObject(origin, parameters);
            if (enclosingClass == parameters[0].getType()) {
                Field f = getFieldValueWithType(clazz, enclosingClass);
                try {
                    // The constructor may retain an enclosing parameter even
                    // when javac omits its unused synthetic field.
                    if (f != null) {
                        f.setAccessible(true);
                        vars[0] = f.get(origin);
                    }
                    c.setAccessible(true);
                    return (T) c.newInstance(vars);
                } catch (IllegalArgumentException |
                        IllegalAccessException | InstantiationException |
                        InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return null;
    }

    private static <T> Object[] getParametersFromObject(T origin,
            Parameter[] constructorParameters) {
        final Class<?> clazz = origin.getClass();
        final IndexedHashMap<String,Field> fieldMap = getAllFieldsByName(clazz);
        final int paramSize = constructorParameters.length;
        Object[] vars = new Object[paramSize];
        for (int i=1; i<paramSize; i++) {
            try {
                vars[i] = getFieldValue(origin, constructorParameters[i].getName());
            } catch (IllegalArgumentException e) {
                // use same index as reported by java reflection
                final String alternativeName = fieldMap.getKeyAtIndex(i-1);
                //System.out.println("ALTERNATIVE NAME: " + alternativeName);
                vars[i] = getFieldValue(origin, alternativeName);
            }
        }
        return vars;
    }

    private static <T> T newInstance(
            Constructor<?> c) throws RuntimeException {
        try {
            c.setAccessible(true);
            return (T) c.newInstance();
        } catch (IllegalArgumentException |
                IllegalAccessException | InstantiationException |
                InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
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
