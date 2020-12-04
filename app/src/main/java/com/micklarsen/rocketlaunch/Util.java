package com.micklarsen.rocketlaunch;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Util {

    //returns the current date in utc timezone
    public static Date getUTCDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }

    //returns the suffix for day, like 13th, 2nd etc.
    public static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    //returns the wind direction text from angle
    public static String getWindDirectionString(int direction) {
        int segment = (int) Math.round((direction / 22.5) + 0.5);
        String[] values = new String[]{"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        return values[(segment % 16)];
    }

    //returns the weather icon
    public static String getWeatherIcon(String icon) {
        return "http://openweathermap.org/img/wn/" + icon + "@2x.png";
    }
}
