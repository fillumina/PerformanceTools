package com.fillumina.performance.util;

import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FluentBuilderTest {

    public static class CallBackBuilderImpl<T>
            extends FluentBuilder<T, String> {

        private String name;

        public CallBackBuilderImpl() {
        }

        public CallBackBuilderImpl(T caller) {
            super(caller);
        }

        public CallBackBuilderImpl(Setter<T, String> setter) {
            super(setter);
        }

        public CallBackBuilderImpl<T> name(String name) {
            this.name = name;
            return this;
        }

        @Override
        protected String build() {
            return name;
        }
    }

    public static class Car {
        private String name;

        public static CallBackBuilderImpl<Car> builder() {
            return new CallBackBuilderImpl<>( (String name) -> {
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

    @Test
    public void shouldCreateACar() {
        Car car = Car.builder().name("Alfa Romeo").end();

        assertEquals("Alfa Romeo", car.getName());
    }

    @Test
    public void shouldReturnTheBuiltObjectUsingTheDefaultConstructor() {
        CallBackBuilderImpl<String> builder = new CallBackBuilderImpl<>();
        builder.name("alpha");

        assertEquals("alpha", builder.build());
    }

    @Test
    public void shouldReturnTheBuiltObjectUsingEndWithTheDefaultConstructor() {
        CallBackBuilderImpl<String> builder = new CallBackBuilderImpl<>();
        builder.name("alpha");

        assertEquals("alpha", builder.end());
    }

    @Test
    public void shouldReturnTheGivenCaller() {
        Object caller = new Object();
        CallBackBuilderImpl<Object> builder = new CallBackBuilderImpl<>(caller);

        assertTrue(caller == builder.end());
    }

    @Test
    public void shouldReturnAValueFromSetter() {
        final AtomicReference<String> str = new AtomicReference<>();
        final Object caller = new Object();
        CallBackBuilderImpl<Object> builder = new CallBackBuilderImpl<>(
                (builtObject) -> {
                    str.set(builtObject);
                    return caller;
                });

        builder.name("alpha");

        assertTrue(caller == builder.end());
        assertEquals("alpha", str.get());
    }
}
