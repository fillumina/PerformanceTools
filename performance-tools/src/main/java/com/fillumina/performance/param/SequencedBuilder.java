package com.fillumina.performance.param;

import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.collection.LinkedTree;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SequencedBuilder<C>
        extends CallBackBuilder<C, LinkedTree<String,Object>> {

    private final LinkedTree<String,Object> root = new LinkedTree<>();

    public SequencedBuilder() {
        super();
    }

    public SequencedBuilder(C caller) {
        super(caller);
    }

    public SequencedBuilder(
            Setter<C, LinkedTree<String, Object>> setter) {
        super(setter);
    }

    public Value addSequence(String name) {
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

        public SequencedBuilder<C> endParameter() {
            return SequencedBuilder.this;
        }
    }

    @Override
    public LinkedTree<String, Object> build() {
        return root;
    }
}
