package com.micklarsen.rocketlaunch;

import java.util.ArrayList;

//class that contains static data that will be used in different activities
public class Data {

    public static String sUrl = "https://micklarsen.com/3_sem_eksamensprojekt/api/";

    //all rocket laucnhes are saved in this object after being loaded
    public static ArrayList<RocketLaunch> sRocketLaunches = new ArrayList<>();

    public static RocketLaunch getLaunchById(String id) {
        for (RocketLaunch rocketLaunch : sRocketLaunches) {
            if (rocketLaunch.getId().equals(id)) return rocketLaunch;
        }
        return null;
    }
}
