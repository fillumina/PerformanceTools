package com.fillumina.performance.assertion;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AddableMultiAssertion extends MultiAssertionFactory {

    private Collection<Assertion> coll;

    public AddableMultiAssertion() {
        this(new ArrayList<>());
    }

    public AddableMultiAssertion(Collection<Assertion> coll) {
        super(coll);
        this.coll = coll;
    }

    public void addAssertion(Assertion assertion) {
        coll.add(assertion);
    }

}
