package com.fillumina.performance.producer.suite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SequenceNominator<S> {
    String toString(final S sequenceItem);

    final static SequenceNominator<Object> DEFAULT =
            new SequenceNominator<Object>() {

        @Override
        public String toString(Object sequenceItem) {
            return sequenceItem == null ? "Default" : sequenceItem.toString();
        }
    };
}
