package com.fillumina.performance.executor.param;

import com.fillumina.performance.util.FluentBuilder;
import com.fillumina.performance.util.collection.LinkedTree;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SubTreeBuilder<C>
        extends FluentBuilder<C, LinkedTree<String,Object>> {

    private final LinkedTree<String,Object> root;

    public SubTreeBuilder() {
        super();
        root = new LinkedTree<>();
    }

    public SubTreeBuilder(C caller,
            LinkedTree<String,Object> root) {
        super(caller);
        this.root = root;
    }

    public SubTreeBuilder(
            Setter<C, LinkedTree<String, Object>> setter,
            LinkedTree<String,Object> root) {
        super(setter);
        this.root = root;
    }

    /**
     * The name of the field annotated with
     * {@link com.fillumina.performance.executor.annotation.Param}
     * or {@link com.fillumina.performance.executor.annotation.Sequence}
     * or the value of the 'value' parameter of these annotations.
     */
    public Value name(String name) {
        return new Value(name, root.add(name, null));
    }

    public class Value {
        private final String name;
        private final LinkedTree<String,Object> current;

        public Value(String name, LinkedTree<String, Object> current) {
            this.name = name;
            this.current = current;
        }

        /**
         * The name of this specific parameter to be referred in tests
         * and {@link ExperimentAssertion}s.
         *
         * @param obj
         * @return
         */
        private String name(Object obj) {
            String str = Objects.toString(obj);
            return name != null ? name + "_" + str : str;
        }

        /**
         * Specifies a name for the value, all other methods will try to
         * deduce a name by using {@link Object#toString()}.
         */
        public Value value(String name, Object value) {
            current.put(name, value);
            return this;
        }

        public Value value(Object value) {
            current.put(name(value), value);
            return this;
        }

        public Value values(Object... values) {
            for (Object v : values) {
                value(v);
            }
            return this;
        }

        public Value values(Iterable<?> iterable) {
            iterable.forEach(i -> { value(i); } );
            return this;
        }

        public Value values(Stream<?> stream) {
            stream.forEach(i -> { value(i); } );
            return this;
        }

        public SubTreeBuilder<C> end() {
            return SubTreeBuilder.this;
        }
    }

    @Override
    public LinkedTree<String, Object> build() {
        return root;
    }
}
