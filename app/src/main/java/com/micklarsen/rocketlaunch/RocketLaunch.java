package com.micklarsen.rocketlaunch;

import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RocketLaunch {

    private String mId;
    private String mName;
    private Date mLaunchDate;
    private String mProviderName;
    private String mLocation;
    private String mCountryCode;

    private String mLaunchType;
    private String mMissionName;
    private String mMissionDetails;
    private String mRocketName;
    private String mRocketImage;
    private String mProviderUrl;

    private String mWikiLink;
    private String mPadName;
    private double mLatitude = 0;
    private double mLongitude = 0;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getLaunchDate() {
        return mLaunchDate;
    }

    public void setLaunchDate(Date launchDate) {
        mLaunchDate = launchDate;
    }

    public String getProviderName() {
        return mProviderName;
    }

    public void setProviderName(String providerName) {
        mProviderName = providerName;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public String getLaunchType() {
        return mLaunchType;
    }

    public void setLaunchType(String launchType) {
        mLaunchType = launchType;
    }

    public String getMissionName() {
        return mMissionName;
    }

    public void setMissionName(String missionName) {
        mMissionName = missionName;
    }

    public String getMissionDetails() {
        return mMissionDetails;
    }

    public void setMissionDetails(String missionDetails) {
        mMissionDetails = missionDetails;
    }

    public String getRocketName() {
        return mRocketName;
    }

    public void setRocketName(String rocketName) {
        mRocketName = rocketName;
    }

    public String getRocketImage() {
        return mRocketImage;
    }

    public void setRocketImage(String rocketImage) {
        mRocketImage = rocketImage;
    }

    public String getProviderUrl() {
        return mProviderUrl;
    }

    public void setProviderUrl(String providerUrl) {
        mProviderUrl = providerUrl;
    }

    public String getWikiLink() {
        return mWikiLink;
    }

    public void setWikiLink(String wikiLink) {
        mWikiLink = wikiLink;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getPadName() {
        return mPadName;
    }

    public void setPadName(String padName) {
        mPadName = padName;
    }

    public Spanned getTimerString() {
        long remaining = mLaunchDate.getTime() - Util.getUTCDate().getTime();
        long days = Math.max(TimeUnit.MILLISECONDS.toDays(remaining), 0);
        long hours = Math.max(TimeUnit.MILLISECONDS.toHours(remaining) % TimeUnit.DAYS.toHours(1), 0);
        long minutes = Math.max(TimeUnit.MILLISECONDS.toMinutes(remaining) % TimeUnit.HOURS.toMinutes(1), 0);
        long seconds = Math.max(TimeUnit.MILLISECONDS.toSeconds(remaining) % TimeUnit.MINUTES.toSeconds(1), 0);

        StringBuilder builder = new StringBuilder();
        if (days > 4) builder.append("<font color=\"#FF0000\">");
        else if (days > 2) builder.append("<font color=\"#FF5700\">");
        else if (days > 0) builder.append("<font color=\"#C4EF0F\">");
        else builder.append("<font color=\"#0FEF4B\">");
        builder.append(String.format(Locale.getDefault(), "%1$02d", days));
        builder.append("</font>");
        builder.append("d:");

        if (hours > 18 || days > 0) builder.append("<font color=\"#FF0000\">");
        else if (hours > 11) builder.append("<font color=\"#FF5700\">");
        else if (hours > 5) builder.append("<font color=\"#C4EF0F\">");
        else builder.append("<font color=\"#0FEF4B\">");
        builder.append(String.format(Locale.getDefault(), "%1$02d", hours));
        builder.append("</font>");
        builder.append("h:");

        if (minutes > 30 || days > 0 || hours > 0) builder.append("<font color=\"#FF0000\">");
        else if (minutes > 15) builder.append("<font color=\"#FF5700\">");
        else if (minutes > 5) builder.append("<font color=\"#C4EF0F\">");
        else builder.append("<font color=\"#0FEF4B\">");
        builder.append(String.format(Locale.getDefault(), "%1$02d", minutes));
        builder.append("</font>");
        builder.append("m:");

        if (seconds > 45 || days > 0 || hours > 0 || minutes > 0)
            builder.append("<font color=\"#FF0000\">");
        else if (seconds > 30) builder.append("<font color=\"#FF5700\">");
        else if (seconds > 15) builder.append("<font color=\"#C4EF0F\">");
        else builder.append("<font color=\"#0FEF4B\">");
        builder.append(String.format(Locale.getDefault(), "%1$02d", seconds));
        builder.append("</font>");
        builder.append("s");


        return Html.fromHtml(builder.toString());
    }


}
