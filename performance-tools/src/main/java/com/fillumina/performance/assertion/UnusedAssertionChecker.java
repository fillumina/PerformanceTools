package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.Printable;
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
public class UnusedAssertionChecker extends Printable<UnusedAssertionChecker> {
    public static final UnusedAssertionChecker EMPTY =
            new UnusedAssertionChecker(
                    Collections.unmodifiableMap(Collections.emptyMap()) );

    private final Map<ExperimentAssertion, Boolean> checkedMap;

    public UnusedAssertionChecker() {
        checkedMap = new HashMap<>();
    }

    public UnusedAssertionChecker(
            Map<ExperimentAssertion, Boolean> checkedMap) {
        this.checkedMap = checkedMap;
    }

    public void setUsed(ExperimentAssertion assertion) {
        checkedMap.put(assertion, Boolean.TRUE);
    }

    public void setUnused(ExperimentAssertion assertion) {
        if (!checkedMap.containsKey(assertion)) {
            checkedMap.put(assertion, Boolean.FALSE);
        }
    }

    public List<ExperimentAssertion> getUnusedAssertionList() {
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
        return Collections.<ExperimentAssertion>emptyList();
    }

    @Override
    public UnusedAssertionChecker appendTo(Appendable appendable) {
        final List<ExperimentAssertion> list = getUnusedAssertionList();
        if (!list.isEmpty()) {
            AppendableWrapper app = new AppendableWrapper(appendable);
            app.print("Unused Assertions: ");
            app.println(list.size());
            list.forEach(assertion -> {
                app.println(assertion.toString());
            });
            app.newline();
        }
        return this;
    }
}
