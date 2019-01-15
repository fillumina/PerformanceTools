package com.fillumina.performance.executor.param;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OptionContainer {

    private final Map<String, Option> options;

    public static class Builder {

        private Map<String,Option> options = new HashMap<>();

        public Builder addOption(Option option) {
            options.put(option.getOptionName(), option);
            return this;
        }

        public Builder addOption(final String paramName,
                final String optionName, final Object optionValue) {
            options.put(paramName,
                    new Option(paramName, optionName, optionValue));
            return this;
        }

        public OptionContainer build() {
            return new OptionContainer(options);
        }
    }

    public static OptionContainer.Builder builder() {
        return new OptionContainer.Builder();
    }

    private OptionContainer(final Map<String, Option> options) {
        this.options = options;
    }

    public Option getOption(String name) {
        return options.get(name);
    }

    @Override
    public String toString() {
        return "OptionContainer{" + "options=" + options + '}';
    }
}
