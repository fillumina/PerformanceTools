package com.fillumina.performance.executor.param;

import com.fillumina.performance.util.AnnotationHelper;
import com.fillumina.performance.util.ReflectionHelper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class RunnableHelper {

    private final Runnable original;
    private final Class<? extends Runnable> clazz;
    private final Map<String,Field> fieldMap;

    public RunnableHelper(Runnable runnable,
            Class<? extends Annotation> annotation) {
        this.original = runnable;
        this.clazz = runnable.getClass();
        this.fieldMap = new HashMap<>();
        List<Field> fields = AnnotationHelper.getFields(runnable, annotation);
        for (Field f : fields) {
            Annotation annotationInstance = f.getAnnotation(annotation);
            String name = getValue(annotationInstance);
            if ("".equals(name)) {
                name = f.getName();
            }
            fieldMap.put(name, f);
        }
    }

    private String getValue(Annotation annotation) {
        try {
            return (String) annotation.getClass().getMethod("value")
                    .invoke(annotation);
        } catch (NoSuchMethodException | SecurityException |
                IllegalAccessException | IllegalArgumentException |
                InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void set(Object target, String name, Object paramValue) {
        Field f = null;
        try {
            f = fieldMap.get(name);
            f.setAccessible(true);
            f.set(target, paramValue);
        } catch (NullPointerException e) {
            throw new RuntimeException("not existent parameter '" + name + "'",
                    e);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException("cannot set field " + f, ex);
        }
    }

    public Cloner doClone() {
        return new Cloner();
    }

    public Runnable cloneAndSetParameters(Map<String, Object> parameters) {
        return doClone().setParameters(parameters).get();
    }

    class Cloner {
        private final Runnable clone;

        private Cloner() {
            this.clone = newInstance();
            for (Field f : ReflectionHelper.getAllFields(clazz)) {
                f.setAccessible(true);
                try {
                    Object value = f.get(original);
                    f.set(clone, value);
                } catch (IllegalArgumentException |
                        IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        public Runnable get() {
            return clone;
        }

        public Cloner setParameters(Map<String, Object> parameters) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                final String paramName = entry.getKey();
                final Object paramValue = entry.getValue();
                set(paramName, paramValue);
            }
            return this;
        }

        public Cloner set(String name, Object paramValue) {
            RunnableHelper.this.set(clone, name, paramValue);
            return this;
        }

        private Runnable newInstance() {
            return (Runnable) ReflectionHelper.newInstance(original);
        }
    }
}
