package com.micklarsen.rocketlaunch;

import java.util.ArrayList;

public class Data {

    public static String sUrl = "https://micklarsen.com/3_sem_eksamensprojekt/api/";

    public static ArrayList<RocketLaunch> sRocketLaunches = new ArrayList<>();

    public static RocketLaunch getLaunchById(String id) {
        for (RocketLaunch rocketLaunch : sRocketLaunches) {
            if (rocketLaunch.getId().equals(id)) return rocketLaunch;
        }
        return null;
    }
}
