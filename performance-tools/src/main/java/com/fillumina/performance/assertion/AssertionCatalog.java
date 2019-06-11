package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.Printable;
import com.fillumina.performance.util.collection.IndexedHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionCatalog extends Printable<AssertionCatalog> {

    public static final AssertionCatalog EMPTY = new AssertionCatalog(
        IndexedHashMap.emtpy(), IndexedHashMap.emtpy());

    private final Map<AssertableExperiment, List<ExperimentAssertion>>
            failedAssertions;

    private Map<AssertableExperiment, List<ExperimentAssertion>>
            failedAssertionsRO;

    private final Map<AssertableExperiment, List<ExperimentAssertion>>
            successfulAssertions;

    private Map<AssertableExperiment, List<ExperimentAssertion>>
            successfulAssertionsRO;

    public AssertionCatalog() {
        failedAssertions = new IndexedHashMap<>();
        successfulAssertions = new IndexedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    private AssertionCatalog(
            Map<AssertableExperiment, List<ExperimentAssertion>> failed,
            Map<AssertableExperiment, List<ExperimentAssertion>> success) {
        this.failedAssertions = clone(failed);
        this.successfulAssertions = clone(success);
    }

    public boolean add(
            ExperimentAssertion assertion,
            AssertableExperiment assertable) {
        if (assertion.satisfy(assertable)) {
            success(assertion, assertable);
            return true;
        } else {
            fail(assertion, assertable);
            return false;
        }
    }

    public void success(
            ExperimentAssertion assertion,
            AssertableExperiment assertable) {
        successfulAssertionsRO = null;
        addAssertion(successfulAssertions, assertion, assertable);
    }

    public void fail(
            ExperimentAssertion assertion,
            AssertableExperiment assertable) {
        failedAssertionsRO = null;
        addAssertion(failedAssertions, assertion, assertable);
    }

    private void addAssertion(
            Map<AssertableExperiment, List<ExperimentAssertion>> map,
            ExperimentAssertion assertion,
            AssertableExperiment assertable) {
        List<ExperimentAssertion> list = map.get(assertable);
        if (list == null) {
            list = new ArrayList<>();
            map.put(assertable, list);
        }
        list.add(assertion);
    }

    public Map<AssertableExperiment, List<ExperimentAssertion>>
            getFailedAssertions() {
        if (failedAssertionsRO == null) {
            failedAssertionsRO = clone(failedAssertions);
        }
        return failedAssertionsRO;
    }

    public Map<AssertableExperiment, List<ExperimentAssertion>>
            getSuccessfulAssertions() {
        if (successfulAssertionsRO == null) {
            successfulAssertionsRO = clone(successfulAssertions);
        }
        return successfulAssertionsRO;
    }

    private Map<AssertableExperiment, List<ExperimentAssertion>> clone(
            Map<AssertableExperiment, List<ExperimentAssertion>> map) {

        Map<AssertableExperiment, List<ExperimentAssertion>> clone =
                new IndexedHashMap<>(map.size());

        map.forEach((k,l) -> {
            clone.put(k, Collections.unmodifiableList(l));
        });

        return map;
    }

    @Override
    public AssertionCatalog appendTo(Appendable appendable) {
        final AppendableWrapper app = new AppendableWrapper(appendable);
        if (!successfulAssertions.isEmpty()) {
            app.print("Successful Assertions: ");
            app.println(successfulAssertions.size());
            appendTo(appendable, successfulAssertions);
            app.newline();
        }
        if (!failedAssertions.isEmpty()) {
            app.print("Failed Assertions: ");
            app.println(failedAssertions.size());
            appendTo(appendable, failedAssertions);
            app.newline();
        }
        return this;
    }

    private void appendTo(Appendable appendable,
            Map<AssertableExperiment, List<ExperimentAssertion>> map) {
        map.forEach( (AssertableExperiment assertable,
                    List<ExperimentAssertion> assertions) -> {
                try {
                    for (ExperimentAssertion a : assertions) {
                        a.appendTo(appendable, assertable);
                        appendable.append(System.lineSeparator());
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
        });

    }
}
