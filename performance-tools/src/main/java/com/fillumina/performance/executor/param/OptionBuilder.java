package com.fillumina.performance.executor.param;

import com.fillumina.performance.util.FluentBuilder;
import com.fillumina.performance.util.collection.IndexedHashMap;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OptionBuilder<C> extends FluentBuilder<C, Map<String, Option>> {
    private Map<String,Option> options = new IndexedHashMap<>();

    public OptionBuilder() {
        super();
    }

    public OptionBuilder(Setter<C, Map<String, Option>> setter) {
        super(setter);
    }

    public OptionBuilder(C caller) {
        super(caller);
    }

    public OptionBuilder<C> addOption(Option option) {
        options.put(option.getOptionName(), option);
        return this;
    }

    public OptionBuilder<C> addOption(final String paramName,
            final String optionName, final Object optionValue) {
        options.put(paramName,
                new Option(paramName, optionName, optionValue));
        return this;
    }

    @Override
    public Map<String, Option> build() {
        return Collections.unmodifiableMap(options);
    }
}

