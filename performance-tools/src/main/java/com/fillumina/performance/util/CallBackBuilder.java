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

        public NamedBuilder<T> setName(String name) {
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
        Car car = Car.builder().setName("Alfa Romeo").end();

        assertEquals("Alfa Romeo", car.getName());
    }
   }
 * </pre>
 *
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class CallBackBuilder<C,B> implements Builder<B>, Reentrant<C> {

    public interface Setter<C,B> {
        C setBuiltObjectAndReturn(B builtObject);
    }

    private final Setter<C,B> setter;

    @SuppressWarnings("unchecked")
    public CallBackBuilder() {
        this((Setter<C,B>)null);
    }

    @SuppressWarnings("unchecked")
    public CallBackBuilder(C caller) {
        if (caller == null) {
            this.setter = (builtObject) -> { return (C) builtObject; };
        } else {
            this.setter = (builtObject) -> { return caller; };
        }
    }

    @SuppressWarnings("unchecked")
    public CallBackBuilder(Setter<C, B> setter) {
        if (setter == null) {
            this.setter = (builtObject) -> { return (C) builtObject; };
        } else {
            this.setter = setter;
        }
    }

    // end() was preferred to build() because of how it shows in a
    // complex fluid interface builder.
    @Override
    public C end() {
        return setter.setBuiltObjectAndReturn(build());
    }
}
