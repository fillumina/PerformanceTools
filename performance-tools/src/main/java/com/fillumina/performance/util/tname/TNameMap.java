package com.fillumina.performance.util.tname;

import com.fillumina.performance.util.collection.ArrayMap;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMap<T extends TNamed> extends ArrayMap<TName, T> {

    private static final Function<TNamed, TName> TNAME_EXTRACTOR =
            t -> t.getTestName();

    @SuppressWarnings("unchecked")
    private static <T extends TNamed> Function<T,TName> getDefaultExtractor() {
        return (Function<T, TName>) TNAME_EXTRACTOR;
    }

    public TNameMap() {
        super(getDefaultExtractor());
    }

    public TNameMap(int size) {
        super(getDefaultExtractor(), size);
    }

    public TNameMap(List<T> list) {
        super(getDefaultExtractor(), list);
    }

    public T get(CharSequence testName) {
        String nameStr = testName.toString();
        return getByKey(t -> t.equals(testName) || nameStr.equals(t.toString()));
    }
}
