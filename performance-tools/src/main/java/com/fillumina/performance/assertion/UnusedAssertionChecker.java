package com.fillumina.performance.assertion;

import com.fillumina.performance.util.collection.UnmodifiableList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnusedAssertionChecker {
    private Map<Assertion, Boolean> checkedMap = new HashMap<>();

    public void setUsed(Assertion assertion) {
        checkedMap.put(assertion, Boolean.TRUE);
    }

    public void setUnused(Assertion assertion) {
        if (!checkedMap.containsKey(assertion)) {
            checkedMap.put(assertion, Boolean.FALSE);
        }
    }

    public List<Assertion> getFailedAssertions() {
        if (!checkedMap.isEmpty()) {
            Iterator<Boolean> it = checkedMap.values().iterator();
            while (it.hasNext()) {
                // avoid unboxing
                if (it.next() == Boolean.TRUE) {
                    it.remove();
                }
            }
        }
        if (!checkedMap.isEmpty()) {
            return new UnmodifiableList<>(checkedMap.keySet());
        }
        return Collections.<Assertion>emptyList();
    }
}
