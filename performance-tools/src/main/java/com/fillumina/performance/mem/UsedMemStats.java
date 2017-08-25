package com.fillumina.performance.mem;

import com.fillumina.performance.util.tname.TName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemStats extends MemStats {
    private static final long serialVersionUID = 1L;

    public UsedMemStats(Map<TName, SingleMemStats> map) {
        super(map);
    }

}
