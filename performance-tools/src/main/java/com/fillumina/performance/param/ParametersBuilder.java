package com.fillumina.performance.param;

import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.collection.LinkedTree;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametersBuilder<C>
        extends CallBackBuilder<C, LinkedTree<String,Object>> {

    private final LinkedTree<String,Object> root = new LinkedTree<>();

    public ParametersBuilder() {
        super();
    }

    public ParametersBuilder(C caller) {
        super(caller);
    }

    public ParametersBuilder(
            Setter<C, LinkedTree<String, Object>> setter) {
        super(setter);
    }

    public Value addParameter(String name) {
        return new Value(root.addTree(name, null));
    }

    public class Value {
        private final LinkedTree<String,Object> current;

        public Value(LinkedTree<String, Object> current) {
            this.current = current;
        }

        public Value addValue(String name, Object value) {
            current.put(name, value);
            return this;
        }

        public ParametersBuilder<C> endParameter() {
            return ParametersBuilder.this;
        }
    }

    @Override
    public LinkedTree<String, Object> build() {
        return root;
    }
}
