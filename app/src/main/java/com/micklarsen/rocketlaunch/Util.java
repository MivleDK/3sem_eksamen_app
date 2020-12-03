package com.micklarsen.rocketlaunch;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Util {

    public static Date getUTCDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }
}
