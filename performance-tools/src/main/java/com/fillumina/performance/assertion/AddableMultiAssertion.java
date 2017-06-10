package com.fillumina.performance.assertion;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AddableMultiAssertion<A extends Assertable>
        extends MultiAssertionFactory<A> {

    private Collection<Assertion<A>> coll;

    public AddableMultiAssertion() {
        this(new ArrayList<>());
    }

    public AddableMultiAssertion(Collection<Assertion<A>> coll) {
        super(coll);
        this.coll = coll;
    }

    public void addAssertion(Assertion<A> assertion) {
        coll.add(assertion);
    }

}
