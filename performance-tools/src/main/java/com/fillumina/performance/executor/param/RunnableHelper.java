package com.fillumina.performance.executor.param;

import com.fillumina.performance.util.reflection.AnnotationHelper;
import com.fillumina.performance.util.reflection.ReflectionHelper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class RunnableHelper {
    private final Runnable original;
    private final Map<String,Field> fieldMap;

    public RunnableHelper(Runnable runnable,
            Class<? extends Annotation> annotation) {
        this.original = runnable;
        this.fieldMap = AnnotationHelper
                .createAssignableFieldsMap(annotation, runnable);
    }

    public void set(Object target, String name, Object value) {
        Field f = null;
        try {
            f = fieldMap.get(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (NullPointerException e) {
            throw new RuntimeException("parameter not found: '" + name + "'", e);
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
            this.clone = ReflectionHelper.clone(original);
        }

        public Runnable get() {
            return clone;
        }

        public Cloner setParameters(Map<String, Object> parameters) {
            parameters.forEach((String paramName, Object paramValue) ->
                set(paramName, paramValue) );
            return this;
        }

        public Cloner set(String name, Object paramValue) {
            RunnableHelper.this.set(clone, name, paramValue);
            return this;
        }
    }
}
