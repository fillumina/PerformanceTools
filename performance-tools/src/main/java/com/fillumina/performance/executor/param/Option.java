package com.fillumina.performance.executor.param;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Option {

    private final String paramName;
    private final String optionName;
    private final Object optionValue;

    public Option(String paramName, String optionName, Object optionValue) {
        this.paramName = paramName;
        this.optionName = optionName;
        this.optionValue = optionValue;
    }

    public String getParamName() {
        return paramName;
    }

    public String getOptionName() {
        return optionName;
    }

    @SuppressWarnings(value = "unchecked")
    public <T> T getOptionValue() {
        return (T) optionValue;
    }

    @Override
    public String toString() {
        return "ParamInfo{" + "paramName=" + paramName + ", optionName=" +
                optionName + ", optionValue=" + optionValue + '}';
    }

}
