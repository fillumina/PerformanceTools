package com.fillumina.performance.stats.baseline;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class BaselineHelper {
    private static final String BASELINE_TEST_NAME = "[BASELINE[[";

    public static final BaselineHelper INSTANCE = new BaselineHelper();

    protected BaselineHelper() {}

    public void removeAllBaseline(Map<String, ?> tests) {
        Iterator<String> it = tests.keySet().iterator();
        while (it.hasNext()) {
            if (isBaseline(it.next())) {
                it.remove();
            }
        }
    }

    public boolean isBaselinePresent(Map<String, ?> tests) {
        Iterator<String> it = tests.keySet().iterator();
        while (it.hasNext()) {
            if (isBaseline(it.next())) {
                return true;
            }
        }
        return false;
    }

    public boolean isBaseline(String name) {
        return name != null && name.startsWith(BASELINE_TEST_NAME);
    }

    public String createBaselineName(Object test) {
        if (test instanceof Baseline) {
            return BASELINE_TEST_NAME + ((Baseline) test).getNanoseconds();
        }
        return BASELINE_TEST_NAME;
    }

    public int extractNanoseconds(String baselineName) {
        String nsStr = baselineName.substring(BASELINE_TEST_NAME.length());
        if (nsStr.isEmpty()) {
            return 0;
        }
        return Integer.valueOf(nsStr);
    }
}
