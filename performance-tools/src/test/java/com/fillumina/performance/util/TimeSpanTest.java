package com.fillumina.performance.util;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSpanTest {

    @Test
    public void shouldGetFromDuration() {
        Duration duration = Duration.ofHours(2);
        assertEquals(2, new TimeSpan(duration).asHours());
    }

    @Test
    public void shouldGetFromDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(123456);
        Date date = cal.getTime();
        TimeSpan ts = TimeSpan.from(date);
        assertEquals(123456, ts.asMillis());
    }

    @Test
    public void shouldGetFromCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(123456);
        TimeSpan ts = TimeSpan.from(cal);
        assertEquals(123456, ts.asMillis());
    }

    @Test
    public void shouldGetTimeInMillis() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.add(Calendar.SECOND, 17);
        cal.add(Calendar.MILLISECOND, 23);
        cal.add(Calendar.MINUTE, 31);
        cal.add(Calendar.HOUR, 13);

        TimeSpan ts = TimeSpan.set().hour(13).min(31).sec(17).millis(23);

        assertEquals(cal.getTimeInMillis(), ts.asMillis());
    }
}
