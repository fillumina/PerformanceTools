package com.fillumina.performance.util;

/**
 * It's useful when you need to use the same builder to create different
 * objects. In practice it allows to specify a setter that assign the
 * result of the builder and returns a specific object.
 * <pre>
 * {@code

    // the generalized builder that builds things with a name
    public static class NamedBuilder<T>
            extends CallBackBuilder<T, String> {

        private String name;

        public NamedBuilder(Setter<T, String> setter) {
            super(setter);
        }

        // builders usually use this fluent notation instead of setters.
        public NamedBuilder<T> name(String name) {
            this.name = name;
            return this;
        }
    }

    // the named object to be built
    public static class Car {
        private String name;

        public static NamedBuilder<Car> builder() {
            return new NamedBuilder<>( (String name) -> {
                return new Car(name);
            });
        }

        private Car(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // test
    public void shouldCreateACar() {
        Car car = Car.builder().name("Alfa Romeo").end();

        assertEquals("Alfa Romeo", car.getName());
    }
   }
 * </pre>
 *
 * @param C returned object type
 * @param B type of the object to build
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class FluentBuilder<C,B> implements Reentrant<C> {

    public interface Setter<C,B> {
        C setBuiltObjectAndReturn(B builtObject);
    }

    private final Setter<C,B> setter;

    protected abstract B build();

    @SuppressWarnings("unchecked")
    public FluentBuilder() {
        this((Setter<C,B>)null);
    }

    @SuppressWarnings("unchecked")
    public FluentBuilder(C caller) {
        if (caller == null) {
            this.setter = (builtObject) -> { return (C) builtObject; };
        } else {
            this.setter = (builtObject) -> { return caller; };
        }
    }

    @SuppressWarnings("unchecked")
    public FluentBuilder(Setter<C, B> setter) {
        if (setter == null) {
            this.setter = (builtObject) -> { return (C) builtObject; };
        } else {
            this.setter = setter;
        }
    }

    /**
     * Ends the current builder (there could be a chain of them) and assign
     * it.
     * <b>Always end the builder with this method otherwise the object might
     * not be properly configured or assigned.</b>
     */
    @Override
    public C end() {
        return setter.setBuiltObjectAndReturn(build());
    }
}
