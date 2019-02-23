package com.fillumina.performance.time.sample;

import com.fillumina.performance.util.pathname.PathName;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationLoggerTest {

    public static void main(final String[] args) {
        IterationLogger logger = new IterationLogger(PathName.ROOT.append("apha"), 3);
        logger.log(0, 60, 100, 120, 100.0 / 120.0);
        logger.log(1, 77, 100, 80, 100.0 / 80.0);
        logger.log(2, 50, 100, 70, 100.0 / 70.0);
        System.out.println("" + logger.getMessage());
    }

    @Test
    public void testSomeMethod() {
        IterationLogger logger = new IterationLogger(PathName.ROOT.append("apha"), 3);
        logger.log(0, 60, 100, 120, 100.0 / 120.0);
        logger.log(1, 77, 100, 80, 100.0 / 80.0);
        logger.log(2, 50, 100, 70, 100.0 / 70.0);

        assertNotNull(logger.getMessage());
    }

}
